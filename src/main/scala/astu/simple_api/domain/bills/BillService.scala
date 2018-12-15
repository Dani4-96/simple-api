package astu.simple_api.domain.bills

import cats.Monad

class BillService[F[_]: Monad](repository: BillRepositoryAlgebra[F]) {
  import cats.syntax.all._

  def get(userId: Int): F[List[Bill]] =
    repository.getByUserId(userId)

  def add(bill: BillData, userId: Int): F[Bill] =
    repository.add(bill, userId)

  def delete(id: Int): F[Unit] =
    repository.delete(id).as(())
}

object BillService {
  def apply[F[_]: Monad](repository: BillRepositoryAlgebra[F]): BillService[F] =
    new BillService[F](repository)
}