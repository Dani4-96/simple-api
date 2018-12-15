package astu.simple_api.infrastructure.endpoint

import cats.effect.Effect
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import astu.simple_api.domain.salary.{SalaryData, SalaryService}

class SalaryEndpoints[F[_]: Effect] extends Http4sDsl[F] {

  implicit val salaryDecoder: EntityDecoder[F, SalaryData] = jsonOf

  object UserIdMatcher extends QueryParamDecoderMatcher[Int]("userId")

  private def getSalaryEndpoint(salaryService: SalaryService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "salary" :? UserIdMatcher(userId) =>
        for {
          found <- salaryService.get(userId)
          resp <- Ok(found.asJson)
        } yield resp
    }

  private def addSalaryEndpoint(salaryService: SalaryService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "salary" :? UserIdMatcher(userId) =>
        for {
          salaryData <- req.as[SalaryData]
          returned <- salaryService.add(salaryData, userId)
          resp <- Ok(returned.asJson)
        } yield resp
    }

  private def deleteSalaryEndpoint(salaryService: SalaryService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case DELETE -> Root / "salary" / IntVar(id) =>
        for {
          returned <- salaryService.delete(id)
          resp <- Ok(returned.asJson)
        } yield resp
    }

  def endpoints(salaryService: SalaryService[F]): HttpRoutes[F] =
    getSalaryEndpoint(salaryService) <+>
    addSalaryEndpoint(salaryService) <+>
    deleteSalaryEndpoint(salaryService)
}

object SalaryEndpoints {
  def endpoints[F[_]: Effect](salaryService: SalaryService[F]): HttpRoutes[F] =
    new SalaryEndpoints[F].endpoints(salaryService)
}

