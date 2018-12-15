package astu.simple_api.domain.shopping

import cats.Monad

class ShoppingService[F[_]: Monad](repository: ShoppingRepositoryAlgebra[F]) {
  import cats.syntax.all._

  def get(userId: Int): F[List[Shopping]] =
    repository.getByUserId(userId)

  def add(shopping: ShoppingData, userId: Int): F[Shopping] =
    repository.add(shopping, userId)

  def delete(id: Int): F[Unit] =
    repository.delete(id).as(())
}

object ShoppingService {
  def apply[F[_]: Monad](repository: ShoppingRepositoryAlgebra[F]): ShoppingService[F] =
    new ShoppingService[F](repository)
}