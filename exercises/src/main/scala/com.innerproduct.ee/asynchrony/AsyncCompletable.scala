package com.innerproduct.ee.asynchrony

import cats.effect._
import java.util.concurrent.CompletableFuture
import scala.jdk.FunctionConverters._

object AsyncCompletable extends IOApp.Simple {
  def run: IO[Unit] = effect.void

  val effect: IO[String] =
    debugCF(IO(cf())) >> debugCF(IO(failingFuture)) !> debugCF(IO(cf("yeah!")))

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

  def debugCF[A](cfa: IO[CompletableFuture[A]]): IO[A] = fromCF(cfa).debug()

  def cf(msg: String = "woo!"): CompletableFuture[String] =
    CompletableFuture.supplyAsync(() => msg) // <3>

  def failingFuture: CompletableFuture[String] =
    CompletableFuture.failedFuture(new IllegalStateException("Something wrong!"))
}
