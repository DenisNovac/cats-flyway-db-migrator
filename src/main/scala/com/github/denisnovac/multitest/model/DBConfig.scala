package com.github.denisnovac.multitest.model

import pureconfig.ConfigReader
import pureconfig.generic.semiauto._

case class DBConfig(
    driver: String,
    url: String,
    user: String,
    password: String,
    migrationsLocation: String,
    migrateOnStart: Boolean,
    threads: Int
)

object DBConfig {
  implicit val config: ConfigReader[DBConfig] = deriveReader[DBConfig]
}
