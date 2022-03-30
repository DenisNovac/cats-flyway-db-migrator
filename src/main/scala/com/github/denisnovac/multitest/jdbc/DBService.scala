package com.github.denisnovac.multitest.jdbc

import cats.data.Kleisli
import cats.implicits._
import cats.effect.{Async, Resource}
import com.github.denisnovac.multitest.model.DBConfig
import doobie.ExecutionContexts
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.MigrationState
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.annotation.nowarn
import scala.jdk.CollectionConverters._

class DBService[F[_]: Async: Logger](config: DBConfig) {

  private def logValidationErrorsIfAny(flywayConfig: FluentConfiguration): F[Unit] =
    for {
      validated <- Async[F].delay(
                     flywayConfig
                       .ignoreMigrationPatterns("*:pending")
                       .load()
                       .validateWithResult
                   )

      _ <- Async[F].whenA(!validated.validationSuccessful)(
             validated.invalidMigrations.asScala.toList.traverse(error => Logger[F].error(s"Invalid migration: $error"))
           )
      _ <- Async[F].whenA(!validated.validationSuccessful)(
             Async[F].raiseError(new Error("Migrations validation failed (see the logs)"))
           )
    } yield ()

  private def migrationEffect: F[Int] =
    for {

      flywayConfig <- Async[F].delay(
                        Flyway.configure
                          .loggers("log4j2")
                          .dataSource(
                            config.url,
                            config.user,
                            config.password
                          )
                          .group(true)
                          .outOfOrder(false)
                          .locations(config.migrationsLocation)
                          .failOnMissingLocations(true)
                          .baselineOnMigrate(true)
                      )

      _ <- logValidationErrorsIfAny(flywayConfig)
      _ <- Logger[F].info("Migrations validation successful")

      count <- Async[F].delay(flywayConfig.load().migrate().migrationsExecuted)

      _ <- flywayConfig.load().info().all().toList.traverse[F, Unit] { i =>
             i.getState match {
               case MigrationState.SUCCESS => Async[F].unit
               case e                      =>
                 Async[F]
                   .raiseError(new Error(s"Migration ${i.getDescription} status is not \"SUCCESS\": ${e.toString}"))
             }
           }

    } yield count

  private def migrate: F[Unit] =
    for {
      _     <- Logger[F].info(s"Starting the migration for host: ${config.url}")
      count <- migrationEffect
      _     <- Logger[F].info(s"Successful migrations: $count")
    } yield ()

  private def transactor: Resource[F, HikariTransactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](config.threads)
      xa <- HikariTransactor.newHikariTransactor[F](
              config.driver,
              config.url,
              config.user,
              config.password,
              ce
            )
    } yield xa

}

@nowarn // tpolecat can't see that logger is used implicitly in DBService creation :(
object DBService {
  def make[F[_]: Async]: Kleisli[Resource[F, *], DBConfig, HikariTransactor[F]] =
    Kleisli { config =>
      for {
        implicit0(logger: Logger[F]) <- Resource.eval(Slf4jLogger.create[F])
        service                       = new DBService[F](config)
        _                            <- Resource.eval(Async[F].whenA(config.migrateOnStart)(service.migrate))
        xa                           <- service.transactor
      } yield xa
    }
}
