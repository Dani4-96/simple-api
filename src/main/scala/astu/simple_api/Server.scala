package astu.simple_api

import domain.bills.BillService
import infrastructure.endpoint.BillEndpoints
import infrastructure.repository.doobie.DoobieBillRepositoryInterpreter
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
      services      =  BillEndpoints.endpoints[F](billService)
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
