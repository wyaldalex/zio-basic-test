package com.tudux

import zio._

import java.io.FileNotFoundException
import scala.io.{BufferedSource, Source}

object ZioPart2Either extends App {

  //zio moral equivalent of throw????
  val zEither = ZIO.fail("This causes error").either

  val zMatch = zEither.map {
    case Right(success) => success
    case Left(exception) => s"Oops it failed with an exception $exception"
  }

  val forComp = for {
    value <- zMatch
    - <- zio.console.putStrLn(value)
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = forComp.exitCode
}


object CatchAllTest extends App {

  val zFailed: IO[String, Nothing] = ZIO.fail("Some failure")

  val zCatchAll: ZIO[Any, Nothing, String] = zFailed.catchAll{ _ =>
    ZIO.succeed("I recover from fail")
  }

  val forComp = for {
    value <- zCatchAll
    _ <- zio.console.putStrLn(value)
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = forComp.exitCode

}


object CatchSomeTest extends App {

  val fileReader= IO(Source.fromFile("PrimaryFile.txt")).catchSome {
    case _: FileNotFoundException => IO(Source.fromFile("BackupFile.txt"))
  }

  val forComp = for {
    source <- fileReader
    value <- IO(source.getLines().mkString("\n"))
    _ <- zio.console.putStrLn(value)
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = forComp.exitCode
}

object FallBackExample extends App {

  val fileReader = IO(Source.fromFile("Primary.txt")).orElse(IO(Source.fromFile("BackupFile.txt")))

  val forComp = for {
    source <- fileReader
    value <- IO(source.getLines().mkString("\n"))
    _ <- zio.console.putStrLn(value)
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = forComp.exitCode

}

object FoldingExample extends App {


  val fileReader: ZIO[Any, Throwable, BufferedSource] = IO(Source.fromFile("Primary.txt")).foldM(
    _ => IO(Source.fromFile("BackupFile.txt")), //catches anything of type Throwable
    data => ZIO.succeed(data)
  )

  val forComp = for {
    source <- fileReader
    value <- IO(source.getLines().mkString("\n"))
    _ <- zio.console.putStrLn(value)
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = forComp.exitCode
}


object ZIORetryExample extends App {

  val fileReader: ZIO[Any, Throwable, BufferedSource] = {
    IO(Source.fromFile("Primary.txt"))
      .retryN(5)
      .orElse(IO(Source.fromFile("BackupFile.txt")))
  }

  val forComp = for {
    source <- fileReader
    value <- IO(source.getLines().mkString("\n"))
    _ <- zio.console.putStrLn(value)
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = forComp.exitCode
}
