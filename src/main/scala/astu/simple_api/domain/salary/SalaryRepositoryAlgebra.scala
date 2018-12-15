package astu.simple_api.domain.salary

trait SalaryRepositoryAlgebra[F[_]] {
  def getByUserId(userId: Int): F[List[Salary]]

  def add(salary: SalaryData, userId: Int): F[Salary]

  def delete(id: Int): F[Int]
}
