package com.github.denisnovac.multitest.model.repos

import com.github.denisnovac.multitest.jdbc.DBContext
import com.github.denisnovac.multitest.model.User
import doobie.ConnectionIO

trait UserRepo[F[_]] {
  def read(id: Int): F[Option[User]]
  def lookup(uKey: String): F[Option[User]]
  def upsert(user: User): F[User]
  def delete(id: Int): F[Unit]
}

class UserRepoImpl extends UserRepo[ConnectionIO] with DBContext {
  import quillContext._

  private val table = quote(querySchema[User]("users"))

  override def read(id: Int): ConnectionIO[Option[User]] =
    run(table.filter(_.uId == lift(id))).map(_.headOption)

  override def lookup(uKey: String): ConnectionIO[Option[User]] =
    run(table.filter(_.uKey == lift(uKey))).map(_.headOption)

  override def upsert(user: User): ConnectionIO[User] =
    run(
      table
        .insertValue(lift(user))
        .onConflictUpdate(_.uId)(
          (t, e) => t.uKey -> e.uKey,
          (t, e) => t.uValue -> e.uValue,
          (t, e) => t.updatedAt -> e.updatedAt
        )
        .returning(u => u)
    )

  override def delete(id: Int): ConnectionIO[Unit] =
    run(
      table.filter(_.uId == lift(id)).delete
    ).map(_ => ())

}
