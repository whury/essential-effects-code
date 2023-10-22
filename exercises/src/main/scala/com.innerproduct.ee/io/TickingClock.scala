package com.innerproduct.ee.io

import cats.effect._
import scala.concurrent.duration.{ FiniteDuration, SECONDS }

object TickingClock extends IOApp.Simple {
  def run: IO[Unit] =
    tickingClock

  val tickingClock: IO[Unit] = for {
    _ <- IO(println(System.currentTimeMillis()))
    _ <- IO.sleep(FiniteDuration(1, SECONDS))
    _ <- tickingClock
  } yield ()
}
