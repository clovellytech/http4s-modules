package com.clovellytech.auth

import cats.effect.IO
import com.clovellytech.db.config.DatabaseConfig
import com.clovellytech.db.loadTransactorFromConfig
import doobie.util.transactor.Transactor

package object infrastructure {
  lazy val testTransactor: Transactor[IO] = loadTransactorFromConfig[IO, DatabaseConfig]("db", "ct_auth")(identity).unsafeRunSync
}
