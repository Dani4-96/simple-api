package astu.simple_api.infrastructure.endpoint

import cats.effect.Effect
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import astu.simple_api.domain.bills.{BillData, BillService}

class BillEndpoints[F[_]: Effect] extends Http4sDsl[F] {

  implicit val billDecoder: EntityDecoder[F, BillData] = jsonOf

  object UserIdMatcher extends QueryParamDecoderMatcher[Int]("userId")

  private def getBillEndpoint(billService: BillService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "bills" :? UserIdMatcher(userId) =>
        for {
          found <- billService.get(userId)
          resp <- Ok(found.asJson)
        } yield resp
    }

  private def addBillEndpoint(billService: BillService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "bills" :? UserIdMatcher(userId) =>
        for {
          billData <- req.as[BillData]
          returned <- billService.add(billData, userId)
          resp <- Ok(returned.asJson)
        } yield resp
    }

  private def deleteBillEndpoint(billService: BillService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case DELETE -> Root / "bills" / IntVar(id) =>
        for {
          returned <- billService.delete(id)
          resp <- Ok(returned.asJson)
        } yield resp
    }

  def endpoints(billService: BillService[F]): HttpRoutes[F] =
    getBillEndpoint(billService) <+>
    addBillEndpoint(billService) <+>
    deleteBillEndpoint(billService)
}

object BillEndpoints {
  def endpoints[F[_]: Effect](billService: BillService[F]): HttpRoutes[F] =
    new BillEndpoints[F].endpoints(billService)
}
