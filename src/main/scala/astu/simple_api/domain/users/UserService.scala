package astu.simple_api.domain.users

import cats.Monad

class UserService[F[_]: Monad](repository: UserRepositoryAlgebra[F]) {
  import cats.syntax.all._

  def get(): F[List[User]] =
    repository.get()

  def add(user: User): F[User] =
    repository.add(user)

  def delete(id: Int): F[Unit] =
    repository.delete(id).as(())
}

object UserService {
  def apply[F[_]: Monad](repository: UserRepositoryAlgebra[F]): UserService[F] =
    new UserService[F](repository)
}