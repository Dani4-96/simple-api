package astu.simple_api

import astu.simple_api.domain.salary.SalaryService
import astu.simple_api.domain.shopping.ShoppingService
import domain.bills.BillService
import infrastructure.endpoint.{BillEndpoints, SalaryEndpoints, ShoppingEndpoints}
import infrastructure.repository.doobie.{DoobieBillRepositoryInterpreter, DoobieSalaryRepositoryInterpreter, DoobieShoppingRepositoryInterpreter}
import config.{DatabaseConfig, SimpleApiConfig}
import cats.effect._
import cats.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._

import scala.concurrent.ExecutionContext.Implicits.global

object Server extends IOApp {
  def createServer[F[_]: ContextShift : ConcurrentEffect : Timer]: Resource[F, ExitCode] =
    for {
      conf            <- Resource.liftF(SimpleApiConfig.load[F])
      xa              <- DatabaseConfig.dbTransactor(conf.db, global, global)
      billRepo        =  DoobieBillRepositoryInterpreter[F](xa)
      billService     =  BillService[F](billRepo)
      salaryRepo      =  DoobieSalaryRepositoryInterpreter[F](xa)
      salaryService   =  SalaryService[F](salaryRepo)
      shoppingRepo    =  DoobieShoppingRepositoryInterpreter[F](xa)
      shoppingService =  ShoppingService[F](shoppingRepo)
      services        =  BillEndpoints.endpoints[F](billService) <+> SalaryEndpoints.endpoints[F](salaryService) <+> ShoppingEndpoints.endpoints[F](shoppingService)
      httpApp         =  Router("/" -> services).orNotFound
      exitCode        <- Resource.liftF(
        BlazeServerBuilder[F]
          .bindHttp(8080, "localhost")
          .withHttpApp(httpApp)
          .serve
          .compile
          .drain
          .as(ExitCode.Success)
      )
    } yield exitCode

  def run(args: List[String]): IO[ExitCode] = createServer.use(IO.pure)
}
