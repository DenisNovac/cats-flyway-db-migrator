package com.github.denisnovac.multitest.services

import cats.effect.Sync
import com.github.denisnovac.multitest.model.Status
import com.github.denisnovac.multitest.model.repos.{StatusRepo, UserRepo}
import doobie.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor

trait UserStatusService[F[_]] {
  def insert(status: Status): F[Status]
  def update(status: Status): F[Status]
  def remove(id: Int): F[Unit]
}

class UserStatusServiceImpl[F[_]: Sync](
    userRepo: UserRepo[ConnectionIO],
    statusRepo: StatusRepo[ConnectionIO],
    xa: Transactor[F]
) extends UserStatusService[F] {

  override def insert(status: Status): F[Status] = {
    for {
      maybeUser <- userRepo.read(status.uId)
      _         <- Sync[ConnectionIO].fromOption(maybeUser, new Error(s"No such user: ${status.uId} to give a status"))
      result    <- statusRepo.upsert(status)
    } yield result
  }.transact(xa)

  override def update(status: Status): F[Status] = {
    for {
      maybeOldStatus <- statusRepo.read(status.uId)
      _              <- Sync[ConnectionIO].fromOption(maybeOldStatus, new Error(s"No status defined before for user ${status.uId}"))
      result         <- statusRepo.upsert(status)
    } yield result
  }.transact(xa)

  override def remove(id: Int): F[Unit] = {
    for {
      maybeOldStatus <- statusRepo.read(id)
      _              <- Sync[ConnectionIO].fromOption(maybeOldStatus, new Error(s"No status $id to delete"))
      _              <- statusRepo.delete(id)
    } yield ()
  }.transact(xa)

}
