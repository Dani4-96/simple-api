package astu.simple_api.domain.salary

import cats.Monad

class SalaryService[F[_]: Monad](repository: SalaryRepositoryAlgebra[F]) {
  import cats.syntax.all._

  def get(userId: Int): F[List[Salary]] =
    repository.getByUserId(userId)

  def add(salary: SalaryData, userId: Int): F[Salary] =
    repository.add(salary, userId)

  def delete(id: Int): F[Unit] =
    repository.delete(id).as(())
}

object SalaryService {
  def apply[F[_]: Monad](repository: SalaryRepositoryAlgebra[F]): SalaryService[F] =
    new SalaryService[F](repository)
}