package com.tudux

import zio.ZIO.debug
import zio._

object ZioPart4Fibers extends App {

  val wakeUp = ZIO.succeed("Wake Up")
  val doSomething = ZIO.succeed("Doing something")
  val createPr = ZIO.succeed("Creating PR")

  def printThreadName = s"${Thread.currentThread().getName}"

  val forComp = for {
    _ <- wakeUp.debug(printThreadName)
    _ <- doSomething.debug(printThreadName).fork //goes into another thread
    _ <- createPr.debug(printThreadName).fork //goes into another thread
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = forComp.exitCode

}

object ZioJoin extends App {

  val firstTask = ZIO.succeed("First task")
  val secondTask = ZIO.succeed("Second task")
  val thirdTask = ZIO.succeed("Third task")

  def printThreadName = s"${Thread.currentThread().getName}"

  def concurrentTaskOneAndTaskTwo() = for {
    firstFiber <- firstTask.debug(printThreadName).fork
    secondFiber <- secondTask.debug(printThreadName).fork
    combinedFiber = firstFiber.zip(secondFiber)
    result <- combinedFiber.join.debug(printThreadName)
    _ <- ZIO.succeed(s"${result} done!").debug(printThreadName) *> thirdTask.debug(printThreadName)
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = concurrentTaskOneAndTaskTwo.exitCode
}
