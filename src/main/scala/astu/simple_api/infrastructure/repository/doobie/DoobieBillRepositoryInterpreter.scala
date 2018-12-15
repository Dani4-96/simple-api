package astu.simple_api.infrastructure.repository.doobie

import java.sql.Timestamp
import java.time.Instant

import astu.simple_api.domain.bills.{Bill, BillData, BillRepositoryAlgebra}
import cats.Monad
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._

private object BillSQL {
  
  implicit val DateTimeMeta: Meta[Instant] =
    Meta[Timestamp].imap(_.toInstant)(Timestamp.from _)

  def select(userId: Int): Query0[Bill] =
    sql"""
      SELECT id, description, amount, date, user_id
      FROM bill
      WHERE user_id = $userId
    """.query[Bill]

  def insert(billData: BillData, userId: Int): Query0[Bill] = {
    val BillData(description, amount) = billData
    sql"""
      INSERT INTO bill (
        description, amount, date, user_id)
      VALUES ($description, $amount, now(), $userId)
      ON CONFLICT DO NOTHING
      RETURNING id, description, amount, date, user_id
    """.query[Bill]
  }

  def delete(id: Int): Update0 =
    sql"""
      DELETE FROM bill
      WHERE id = $id
    """.update
}

class DoobieBillRepositoryInterpreter[F[_]: Monad](val xa: Transactor[F])
  extends BillRepositoryAlgebra[F] {

  import BillSQL._

  def getByUserId(userId: Int): F[List[Bill]] =
    select(userId).to[List].transact(xa)

  def add(bill: BillData, userId: Int): F[Bill] =
    insert(bill, userId).unique.transact(xa)

  def delete(id: Int): F[Int] =
    BillSQL.delete(id).run.transact(xa)
}

object DoobieBillRepositoryInterpreter {
  def apply[F[_]: Monad](xa: Transactor[F]): DoobieBillRepositoryInterpreter[F] =
    new DoobieBillRepositoryInterpreter(xa)
}
