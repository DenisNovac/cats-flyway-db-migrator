package com.github.denisnovac.multitest.model.repos

import com.github.denisnovac.multitest.jdbc.DBContext
import com.github.denisnovac.multitest.model.Status
import doobie.ConnectionIO

trait StatusRepo[F[_]] {
  def read(id: Int): F[Option[Status]]
  def upsert(status: Status): F[Status]
  def delete(id: Int): F[Unit]
}

class StatusRepoImpl extends StatusRepo[ConnectionIO] with DBContext {
  import quillContext._

  private val table = quote(querySchema[Status]("statuses"))

  override def read(id: Int): ConnectionIO[Option[Status]] =
    run(table.filter(_.uId == lift(id))).map(_.headOption)

  override def upsert(status: Status): ConnectionIO[Status] =
    run(
      table
        .insertValue(lift(status))
        .onConflictUpdate(_.uId)(
          (t, e) => t.uStatus -> e.uStatus,
          (t, e) => t.updatedAt -> e.updatedAt
        )
        .returning(s => s)
    )

  override def delete(id: Int): doobie.ConnectionIO[Unit] =
    run(
      table.filter(_.uId == lift(id)).delete
    ).map(_ => ())

}
