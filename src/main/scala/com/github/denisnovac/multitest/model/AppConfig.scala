package com.github.denisnovac.multitest.model

import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader

case class AppConfig(
    dbConfig: DBConfig
)

object AppConfig {
  implicit val config: ConfigReader[AppConfig] = deriveReader[AppConfig]
}
