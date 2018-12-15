package astu.simple_api.domain.shopping

trait ShoppingRepositoryAlgebra[F[_]] {
  def getByUserId(userId: Int): F[List[Shopping]]

  def add(shopping: ShoppingData, userId: Int): F[Shopping]

  def delete(id: Int): F[Int]
}
