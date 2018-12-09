package astu.simple_api.domain.bills

import astu.simple_api.domain.BillNotFoundError
import cats.Monad
import cats.data.EitherT

class BillService[F[_]: Monad](billRepo: BillRepositoryAlgebra[F]) {

  def getBill(name: String): EitherT[F, BillNotFoundError.type, Bill] =
    EitherT.fromOptionF(billRepo.get(name), BillNotFoundError)
}

object BillService {
  def apply[F[_]: Monad](repository: BillRepositoryAlgebra[F]): BillService[F] =
    new BillService[F](repository)
}