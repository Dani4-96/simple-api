package astu.simple_api.config

import cats.effect.{Async, ContextShift, Resource}
import doobie.hikari.HikariTransactor
import scala.concurrent.ExecutionContext

case class DatabaseConfig(url: String, driver: String, user: String, password: String)

object DatabaseConfig {
  def dbTransactor[F[_]: Async : ContextShift](
    dbc: DatabaseConfig,
    connEc: ExecutionContext,
    transEc: ExecutionContext
  ): Resource[F, HikariTransactor[F]] =
    HikariTransactor.newHikariTransactor[F](dbc.driver, dbc.url, dbc.user, dbc.password, connEc, transEc)
}
