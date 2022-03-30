package com.github.denisnovac.multitest

import cats.data.Kleisli
import cats.effect.kernel.{Async, Resource}
import cats.effect.{ExitCode, IO, IOApp}
import com.github.denisnovac.multitest.jdbc.DBService
import com.github.denisnovac.multitest.model.AppConfig
import doobie.hikari.HikariTransactor
import pureconfig.ConfigSource
import pureconfig.module.catseffect.syntax._

object Main extends IOApp {

  private def app[F[_]: Async]: Kleisli[Resource[F, *], AppConfig, HikariTransactor[F]] = for {
    xa <- DBService.make[F].local[AppConfig](_.dbConfig)
  } yield xa

  private def resource[F[_]: Async] =
    for {
      config     <- Resource.eval(ConfigSource.default.loadF[F, AppConfig]())
      transactor <- app[F].run(config)
    } yield transactor

  override def run(args: List[String]): IO[ExitCode] =
    resource[IO].allocated.flatMap(_._2) >> IO(ExitCode.Success)
}
