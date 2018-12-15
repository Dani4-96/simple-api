package astu.simple_api.domain.users

trait UserRepositoryAlgebra[F[_]] {
  def get(): F[List[User]]

  def add(user: User): F[User]

  def delete(id: Int): F[Int]
}
