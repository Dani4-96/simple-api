package astu.simple_api.domain.bills

trait BillRepositoryAlgebra[F[_]] {
  def getByUserId(userId: Int): F[List[Bill]]

  def add(bill: BillData, userId: Int): F[Bill]

  def delete(id: Int): F[Int]
}
