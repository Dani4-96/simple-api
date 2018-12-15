package astu.simple_api.infrastructure.endpoint

import cats.effect.Effect
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import astu.simple_api.domain.users.{User, UserService}

class UserEndpoints[F[_]: Effect] extends Http4sDsl[F] {

  implicit val shoppingDecoder: EntityDecoder[F, User] = jsonOf

  object UserIdMatcher extends QueryParamDecoderMatcher[Int]("userId")

  private def getUserEndpoint(userService: UserService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "users" =>
        for {
          found <- userService.get()
          resp <- Ok(found.asJson)
        } yield resp
    }

  private def addUserEndpoint(userService: UserService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "users" =>
        for {
          user <- req.as[User]
          returned <- userService.add(user)
          resp <- Ok(returned.asJson)
        } yield resp
    }

  private def deleteUserEndpoint(userService: UserService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case DELETE -> Root / "users" / IntVar(id) =>
        for {
          returned <- userService.delete(id)
          resp <- Ok(returned.asJson)
        } yield resp
    }

  def endpoints(userService: UserService[F]): HttpRoutes[F] =
    getUserEndpoint(userService) <+>
      addUserEndpoint(userService) <+>
      deleteUserEndpoint(userService)
}

object UserEndpoints {
  def endpoints[F[_]: Effect](userService: UserService[F]): HttpRoutes[F] =
    new UserEndpoints[F].endpoints(userService)
}

