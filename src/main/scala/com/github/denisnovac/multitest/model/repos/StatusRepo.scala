package com.github.denisnovac.multitest.model.repos

import com.github.denisnovac.multitest.model.Status
import doobie._
import doobie.implicits._
import doobie.implicits.javatimedrivernative.JavaTimeInstantMeta

trait StatusRepo[F[_]] {
  def read(id: Int): F[Option[Status]]
  def upsert(status: Status): F[Status]
  def delete(id: Int): F[Unit]
}

class StatusRepoImpl extends StatusRepo[ConnectionIO] {

  override def read(id: Int): ConnectionIO[Option[Status]] =
    sql"SELECT * FROM statuses WHERE id = $id".query[Status].option

  override def upsert(status: Status): ConnectionIO[Status] =
    sql"""INSERT INTO statuses VALUES (
      ${status.uId}, ${status.uStatus}, ${status.updatedAt}
    ) ON CONFLICT UPDATE
    """.update.run.map(_ => status)

  override def delete(id: Int): doobie.ConnectionIO[Unit] =
    sql"DELETE FROM statuses WHERE id = $id".update.run.map(_ => ())
}
