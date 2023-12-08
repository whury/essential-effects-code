package com.innerproduct.ee.asynchrony

import cats.effect._
import com.innerproduct.ee.debug._
import java.util.concurrent.CompletableFuture
import scala.jdk.FunctionConverters._

object AsyncCompletable extends IOApp.Simple {
  def run: IO[Unit] =
    effect.debug().void

  val effect: IO[String] =
    fromCF(IO(cf()))

  def fromCF[A](cfa: IO[CompletableFuture[A]]): IO[A] =
    cfa.flatMap { fa =>
      IO.async_ { cb =>
        val handler: (A, Throwable) => Unit = (result, error) => {
          if (error != null) {
            cb(Left(error))
          } else {
            cb(Right(result))
          }
        }

        fa.handle(handler.asJavaBiFunction) // <2>

        ()
      }
    }

  def cf(): CompletableFuture[String] =
    CompletableFuture.supplyAsync(() => "woo!") // <3>
}
