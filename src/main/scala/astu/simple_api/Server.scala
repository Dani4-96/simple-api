package astu.simple_api

import astu.simple_api.domain.salary.SalaryService
import domain.bills.BillService
import infrastructure.endpoint.{BillEndpoints, SalaryEndpoints}
import infrastructure.repository.doobie.{DoobieBillRepositoryInterpreter, DoobieSalaryRepositoryInterpreter}
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
      conf          <- Resource.liftF(SimpleApiConfig.load[F])
      xa            <- DatabaseConfig.dbTransactor(conf.db, global, global)
      billRepo      =  DoobieBillRepositoryInterpreter[F](xa)
      billService   =  BillService[F](billRepo)
      salaryRepo    =  DoobieSalaryRepositoryInterpreter[F](xa)
      salaryService =  SalaryService[F](salaryRepo)
      services      =  BillEndpoints.endpoints[F](billService) <+> SalaryEndpoints.endpoints[F](salaryService)
      httpApp       =  Router("/" -> services).orNotFound
      exitCode      <- Resource.liftF(
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
