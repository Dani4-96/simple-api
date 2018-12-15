package astu.simple_api.infrastructure.repository.doobie

import java.sql.Timestamp
import java.time.Instant

import astu.simple_api.domain.salary.{Salary, SalaryData, SalaryRepositoryAlgebra}
import cats.Monad
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._

private object SalarySQL {

  implicit val DateTimeMeta: Meta[Instant] =
    Meta[Timestamp].imap(_.toInstant)(Timestamp.from _)

  def select(userId: Int): Query0[Salary] =
    sql"""
      SELECT id, description, amount, date, user_id
      FROM salary
      WHERE user_id = $userId
    """.query[Salary]

  def insert(salaryData: SalaryData, userId: Int): Query0[Salary] = {
    val SalaryData(description, amount) = salaryData
    sql"""
      INSERT INTO salary (
        description, amount, date, user_id)
      VALUES ($description, $amount, now(), $userId)
      ON CONFLICT DO NOTHING
      RETURNING id, description, amount, date, user_id
    """.query[Salary]
  }

  def delete(id: Int): Update0 =
    sql"""
      DELETE FROM salary
      WHERE id = $id
    """.update
}

class DoobieSalaryRepositoryInterpreter[F[_]: Monad](val xa: Transactor[F])
  extends SalaryRepositoryAlgebra[F] {

  import SalarySQL._

  def getByUserId(userId: Int): F[List[Salary]] =
    select(userId).to[List].transact(xa)

  def add(salary: SalaryData, userId: Int): F[Salary] =
    insert(salary, userId).unique.transact(xa)

  def delete(id: Int): F[Int] =
    SalarySQL.delete(id).run.transact(xa)
}

object DoobieSalaryRepositoryInterpreter {
  def apply[F[_]: Monad](xa: Transactor[F]): DoobieSalaryRepositoryInterpreter[F] =
    new DoobieSalaryRepositoryInterpreter(xa)
}
