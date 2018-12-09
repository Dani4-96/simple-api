package astu.simple_api.infrastructure.repository.doobie

import astu.simple_api.domain.bills.{Bill, BillRepositoryAlgebra}
import cats.Monad
import doobie._
import doobie.implicits._

private object BillSQL {
  def select(name: String): Query0[Bill] = sql"""
    SELECT name, value
    FROM bill
    WHERE name = $name
  """.query[Bill]
}

class DoobieBillRepositoryInterpreter[F[_]: Monad](val xa: Transactor[F])
  extends BillRepositoryAlgebra[F] {

  import BillSQL._

  def get(name: String): F[Option[Bill]] = select(name).option.transact(xa)
}

object DoobieBillRepositoryInterpreter {
  def apply[F[_]: Monad](xa: Transactor[F]): DoobieBillRepositoryInterpreter[F] =
    new DoobieBillRepositoryInterpreter(xa)
}
