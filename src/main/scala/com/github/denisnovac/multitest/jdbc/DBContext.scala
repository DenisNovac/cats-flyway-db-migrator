package com.github.denisnovac.multitest.jdbc

import io.getquill.SnakeCase
import org.polyvariant.doobiequill.DoobieContext

trait DBContext {
  val quillContext = new DoobieContext.Postgres(SnakeCase)
}
