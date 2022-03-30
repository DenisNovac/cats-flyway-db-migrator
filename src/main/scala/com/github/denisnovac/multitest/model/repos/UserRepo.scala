package com.github.denisnovac.multitest.model.repos

import com.github.denisnovac.multitest.model.User
import doobie._
import doobie.implicits._
import doobie.implicits.javatimedrivernative.JavaTimeInstantMeta // Doobie's Instant codec for SQL

trait UserRepo[F[_]] {
  def read(id: Int): F[Option[User]]
  def lookup(uKey: String): F[Option[User]]
  def upsert(user: User): F[User]
  def delete(id: Int): F[Unit]
}

class UserRepoImpl extends UserRepo[ConnectionIO] {

  override def read(id: Int): ConnectionIO[Option[User]] =
    sql"SELECT * FROM users WHERE id = $id".query[User].option

  override def lookup(uKey: String): ConnectionIO[Option[User]] =
    sql"SELECT * FROM users WHERE u_key = $uKey".query[User].option

  override def upsert(user: User): ConnectionIO[User] =
    sql"""INSERT INTO users VALUES (
      ${user.id}, ${user.uKey}, ${user.uValue}, ${user.createdAt}, ${user.updatedAt}
    ) ON CONFLICT UPDATE
    """.update.run.map(_ => user)

  override def delete(id: Int): doobie.ConnectionIO[Unit] =
    sql"DELETE FROM users WHERE id = $id".update.run.map(_ => ())
}
