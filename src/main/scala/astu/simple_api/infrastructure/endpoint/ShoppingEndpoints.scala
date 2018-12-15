package astu.simple_api.infrastructure.endpoint

import cats.effect.Effect
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import astu.simple_api.domain.shopping.{ShoppingData, ShoppingService}

class ShoppingEndpoints[F[_]: Effect] extends Http4sDsl[F] {

  implicit val shoppingDecoder: EntityDecoder[F, ShoppingData] = jsonOf

  object UserIdMatcher extends QueryParamDecoderMatcher[Int]("userId")

  private def getShoppingEndpoint(shoppingService: ShoppingService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "shopping" :? UserIdMatcher(userId) =>
        for {
          found <- shoppingService.get(userId)
          resp <- Ok(found.asJson)
        } yield resp
    }

  private def addShoppingEndpoint(shoppingService: ShoppingService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "shopping" :? UserIdMatcher(userId) =>
        for {
          shoppingData <- req.as[ShoppingData]
          returned <- shoppingService.add(shoppingData, userId)
          resp <- Ok(returned.asJson)
        } yield resp
    }

  private def deleteShoppingEndpoint(shoppingService: ShoppingService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case DELETE -> Root / "shopping" / IntVar(id) =>
        for {
          returned <- shoppingService.delete(id)
          resp <- Ok(returned.asJson)
        } yield resp
    }

  def endpoints(shoppingService: ShoppingService[F]): HttpRoutes[F] =
    getShoppingEndpoint(shoppingService) <+>
      addShoppingEndpoint(shoppingService) <+>
      deleteShoppingEndpoint(shoppingService)
}

object ShoppingEndpoints {
  def endpoints[F[_]: Effect](shoppingService: ShoppingService[F]): HttpRoutes[F] =
    new ShoppingEndpoints[F].endpoints(shoppingService)
}

