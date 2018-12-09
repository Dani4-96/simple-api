package astu.simple_api.config

import cats.effect.Sync
import cats.implicits._
import pureconfig.error.ConfigReaderException

case class SimpleApiConfig(db: DatabaseConfig)

object SimpleApiConfig {

  import pureconfig._

  def load[F[_]](implicit E: Sync[F]): F[SimpleApiConfig] =
    E.delay(loadConfig[SimpleApiConfig]("simple_api")).flatMap {
      case Right(ok) => E.pure(ok)
      case Left(e) => E.raiseError(new ConfigReaderException[SimpleApiConfig](e))
    }
}
