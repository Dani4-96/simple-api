package astu.simple_api.infrastructure.endpoint

import cats.effect.Effect
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.dsl.Http4sDsl

import astu.simple_api.domain.BillNotFoundError
import astu.simple_api.domain.bills.{Bill, BillService}

class BillEndpoints[F[_]: Effect] extends Http4sDsl[F] {

  implicit val billDecoder: EntityDecoder[F, Bill] = jsonOf

  def getBillEndpoint(billService: BillService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "orders" / name =>
        billService.getBill(name).value.flatMap {
          case Right(found) => Ok(found.asJson)
          case Left(BillNotFoundError) => NotFound("The bill was not found")
        }
    }

  def endpoints(billService: BillService[F]): HttpRoutes[F] =
    getBillEndpoint(billService)
}

object BillEndpoints {
  def endpoints[F[_]: Effect](billService: BillService[F]): HttpRoutes[F] =
    new BillEndpoints[F].endpoints(billService)
}
