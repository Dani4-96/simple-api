package astu.simple_api.domain.bills

trait BillRepositoryAlgebra[F[_]] {
  def get(name: String): F[Option[Bill]]
}
