package astu.simple_api.infrastructure.repository.doobie

import java.sql.Timestamp
import java.time.Instant

import astu.simple_api.domain.users.{User, UserRepositoryAlgebra}
import cats.Monad
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._

private object UserSQL {

  implicit val DateTimeMeta: Meta[Instant] =
    Meta[Timestamp].imap(_.toInstant)(Timestamp.from _)

  def select(): Query0[User] =
    sql"""
      SELECT id, name
      FROM "user"
    """.query[User]

  def insert(user: User): Query0[User] = {
    val User(id, name) = user
    sql"""
      INSERT INTO "user" (
        id, name)
      VALUES ($id, $name)
      ON CONFLICT DO NOTHING
      RETURNING id, name
    """.query[User]
  }

  def delete(id: Int): Update0 =
    sql"""
      DELETE FROM "user"
      WHERE id = $id
    """.update
}

class DoobieUserRepositoryInterpreter[F[_]: Monad](val xa: Transactor[F])
  extends UserRepositoryAlgebra[F] {

  import UserSQL._

  def get(): F[List[User]] =
    select().to[List].transact(xa)

  def add(user: User): F[User] =
    insert(user).unique.transact(xa)

  def delete(id: Int): F[Int] =
    UserSQL.delete(id).run.transact(xa)
}

object DoobieUserRepositoryInterpreter {
  def apply[F[_]: Monad](xa: Transactor[F]): DoobieUserRepositoryInterpreter[F] =
    new DoobieUserRepositoryInterpreter(xa)
}
