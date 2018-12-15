package astu.simple_api.infrastructure.repository.doobie

import java.sql.Timestamp
import java.time.Instant

import astu.simple_api.domain.shopping.{Shopping, ShoppingData, ShoppingRepositoryAlgebra}
import cats.Monad
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._

private object ShoppingSQL {

  implicit val DateTimeMeta: Meta[Instant] =
    Meta[Timestamp].imap(_.toInstant)(Timestamp.from _)

  def select(userId: Int): Query0[Shopping] =
    sql"""
      SELECT id, description, amount, date, user_id
      FROM shopping
      WHERE user_id = $userId
    """.query[Shopping]

  def insert(shoppingData: ShoppingData, userId: Int): Query0[Shopping] = {
    val ShoppingData(description, amount) = shoppingData
    sql"""
      INSERT INTO shopping (
        description, amount, date, user_id)
      VALUES ($description, $amount, now(), $userId)
      ON CONFLICT DO NOTHING
      RETURNING id, description, amount, date, user_id
    """.query[Shopping]
  }

  def delete(id: Int): Update0 =
    sql"""
      DELETE FROM shopping
      WHERE id = $id
    """.update
}

class DoobieShoppingRepositoryInterpreter[F[_]: Monad](val xa: Transactor[F])
  extends ShoppingRepositoryAlgebra[F] {

  import ShoppingSQL._

  def getByUserId(userId: Int): F[List[Shopping]] =
    select(userId).to[List].transact(xa)

  def add(shopping: ShoppingData, userId: Int): F[Shopping] =
    insert(shopping, userId).unique.transact(xa)

  def delete(id: Int): F[Int] =
    ShoppingSQL.delete(id).run.transact(xa)
}

object DoobieShoppingRepositoryInterpreter {
  def apply[F[_]: Monad](xa: Transactor[F]): DoobieShoppingRepositoryInterpreter[F] =
    new DoobieShoppingRepositoryInterpreter(xa)
}
