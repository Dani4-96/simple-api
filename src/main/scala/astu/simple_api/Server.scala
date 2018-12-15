package astu.simple_api

import astu.simple_api.domain.salary.SalaryService
import astu.simple_api.domain.shopping.ShoppingService
import astu.simple_api.domain.users.UserService
import domain.bills.BillService
import infrastructure.endpoint.{BillEndpoints, SalaryEndpoints, ShoppingEndpoints, UserEndpoints}
import infrastructure.repository.doobie.{DoobieBillRepositoryInterpreter, DoobieSalaryRepositoryInterpreter, DoobieShoppingRepositoryInterpreter, DoobieUserRepositoryInterpreter}
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
      userRepo        =  DoobieUserRepositoryInterpreter[F](xa)
      userService     =  UserService[F](userRepo)
      services        =  BillEndpoints.endpoints[F](billService) <+>
                            SalaryEndpoints.endpoints[F](salaryService) <+>
                            ShoppingEndpoints.endpoints[F](shoppingService) <+>
                            UserEndpoints.endpoints[F](userService)
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
