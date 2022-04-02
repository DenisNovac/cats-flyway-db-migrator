package com.github.denisnovac.multitest.jdbc

import cats.effect.{IO, Resource}
import com.github.denisnovac.multitest.model.DBConfig
import org.testcontainers.containers.PostgreSQLContainer
import weaver.{GlobalResource, GlobalWrite, LowPriorityImplicits}

object SharedPostgresContainer extends GlobalResource with LowPriorityImplicits {

  private val container = Resource.make(IO(new PostgreSQLContainer("postgres:alpine")).map { c =>
    c.start()
    println(s"Started postgresql container ${c.getJdbcUrl}")
    c
  }) { c =>
    println(s"Closing postgresql container ${c.getJdbcUrl}")
    IO(c.stop())
  }

  override def sharedResources(global: GlobalWrite): Resource[IO, Unit] =
    for {
      c <- container

      xa <- DBService
              .make[IO]
              .run(
                DBConfig(
                  driver = c.getDriverClassName,
                  url = c.getJdbcUrl,
                  user = c.getUsername,
                  password = c.getPassword,
                  migrationsLocation = "classpath:flyway",
                  migrateOnStart = true,
                  threads = 10
                )
              )
      _  <- global.putR(xa)(classBasedInstance)
    } yield ()

}
