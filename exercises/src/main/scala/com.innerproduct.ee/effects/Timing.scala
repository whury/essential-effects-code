package com.innerproduct.ee.effects

import scala.concurrent.duration.{FiniteDuration, MILLISECONDS}

object Timing extends App {
  val clock: MyIO[Long] = MyIO(() => System.currentTimeMillis)

  def time[A](action: MyIO[A]): MyIO[(FiniteDuration, A)] =
    for {
      start <- clock
      result <- action
      end <- clock
    } yield (FiniteDuration(end-start, MILLISECONDS), result)

  val timedHello = Timing.time(MyIO.putStr("hello"))

  timedHello.unsafeRun() match {
    case (duration, _) => println(s"'hello' took $duration")
  }
}
