package com.tudux

import zio._
import zio.console.putStrLn

import java.io.{BufferedReader, FileNotFoundException, FileReader}
import scala.io.{BufferedSource, Source}

object FinalizerExample extends App {
  val finalizer = UIO.effectTotal(println("Closing the Gods on Finalizer"))

  val finalized: IO[String, Unit] =
    IO.fail("Failed!").ensuring(finalizer) //similar to finally in a try catch

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = finalized.exitCode
}

object BracketImpl extends App {
  /* ZIOs way of a try with resource */
  def lines(file: String): Task[String] = {
    def readLines(reader: BufferedReader): Task[String] = Task.effect(reader.readLine())
    def releaseReader(reader: BufferedReader): UIO[Unit] = Task.effectTotal(reader.close())
    def acquireReader(file: String): Task[BufferedReader] = Task.effect(new BufferedReader(new FileReader(file)))

    Task.bracket(acquireReader(file),releaseReader,readLines)
  }

  val printLinesToConsole = for {
    line <- lines("BackupFile.txt")
    _ <- putStrLn(line)
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = printLinesToConsole.exitCode

}
