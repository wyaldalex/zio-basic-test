package com.tudux

import zio._
import zio.console.{getStrLn, putStrLn}

import scala.io.StdIn

object HelloZio extends App {

  val throwOnConsole = putStrLn("Hello from Zio")

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = throwOnConsole.exitCode
}

object ChainedEffects extends App {

  //example 2
  val forComp = for {
    name <- getStrLn
    - <- putStrLn(s"Output from read: $name" )
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = forComp.exitCode
}

object SequentialOperations extends App {

  val firstName = ZIO.effect(StdIn.readLine("What is your first name"))
  val lastName = ZIO.effect(StdIn.readLine("What is your last name"))


  val zipWithSequence = firstName.zipWith(lastName)((firstName,lastName) => s"$firstName $lastName").map(f => println(f))

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = zipWithSequence.exitCode
}

object SequentialOperations2 extends App {

  val stringNum = ZIO.succeed("42")
  val num = ZIO.succeed(43)
  val zipSequence = stringNum.zip(num).map(println) //combine into 1

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = zipSequence.exitCode
}

object SequentialOperations3 extends App {

  val zipLeftSequence = putStrLn("What is your name") <* getStrLn //same behavior different order
  val zipRightSequence =   getStrLn *> putStrLn("What is your name")
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = zipRightSequence.exitCode
}