package com.tudux

import zio._
import zio.console.putStrLn

object HelloZio extends App {

  val throwOnConsole = putStrLn("Hello from Zio")

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = throwOnConsole.exitCode
}
