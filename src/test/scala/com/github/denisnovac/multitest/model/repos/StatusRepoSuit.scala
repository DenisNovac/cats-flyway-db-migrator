package com.github.denisnovac.multitest.model.repos

import cats.effect.{IO, Resource}
import com.github.denisnovac.multitest.model.{Status, User}
import doobie.hikari.HikariTransactor
import weaver.{GlobalRead, IOSuite, LowPriorityImplicits}
import doobie.implicits._

import java.time.Instant

class StatusRepoSuit(global: GlobalRead) extends IOSuite with LowPriorityImplicits {

  override type Res = HikariTransactor[IO]

  override def sharedResource: Resource[IO, Res] =
    global.getOrFailR[HikariTransactor[IO]](None)(classBasedInstance)

  private val urepo = new UserRepoImpl
  private val repo  = new StatusRepoImpl

  test("CRUD for StatusRepo") { xa =>

    val expectedStatus = Status(100, "test", Instant.now())
    val modifiedStatus = Status(100, "test2", Instant.now())

    {
      for {
        _ <- urepo.upsert(User(100, "test", "test", Instant.now(), Instant.now()))

        e  <- repo.upsert(expectedStatus)
        er <- repo.read(100)

        m  <- repo.upsert(modifiedStatus)
        mr <- repo.read(100)

        _ <- repo.delete(100)
        n <- repo.read(100)

      } yield expect(e == expectedStatus)
        .and(expect(er.contains(expectedStatus)))
        .and(expect(m == modifiedStatus))
        .and(expect(mr.contains(modifiedStatus)))
        .and(expect(n.isEmpty))
    }.transact(xa)
  }
}
