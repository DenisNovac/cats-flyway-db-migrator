package com.github.denisnovac.multitest.model.repos

import cats.effect.{IO, Resource}
import com.github.denisnovac.multitest.model.User
import doobie.hikari.HikariTransactor
import doobie.implicits._
import weaver.{GlobalRead, IOSuite, LowPriorityImplicits}

import java.time.Instant

class UserRepoSuit(global: GlobalRead) extends IOSuite with LowPriorityImplicits {

  override type Res = HikariTransactor[IO]

  override def sharedResource: Resource[IO, Res] =
    global.getOrFailR[HikariTransactor[IO]](None)(classBasedInstance)

  private val repo = new UserRepoImpl

  test("CRUD for UserRepo") { xa =>
    val expectedUser     = User(1, "test", "test", Instant.now(), Instant.now())
    val modifiedExpected = expectedUser.copy(uValue = "test2")

    {
      for {
        i <- repo.upsert(expectedUser)
        r <- repo.read(expectedUser.uId)

        m  <- repo.upsert(modifiedExpected)
        ml <- repo.lookup(modifiedExpected.uKey)

        _  <- repo.delete(modifiedExpected.uId)
        d  <- repo.read(modifiedExpected.uId)
        dl <- repo.lookup(modifiedExpected.uKey)

      } yield expect(i == expectedUser)
        .and(expect(r.contains(expectedUser)))
        .and(expect(m == modifiedExpected))
        .and(expect(ml.contains(modifiedExpected)))
        .and(expect(d.orElse(dl).isEmpty))
    }.transact(xa)
  }
}
