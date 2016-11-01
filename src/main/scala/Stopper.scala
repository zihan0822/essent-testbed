package playground

import chisel3._
import Chisel.iotesters.{PeekPokeTester, Driver}


class Stopper extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(width = 2))
  })
  when (io.in === UInt(2)) {
    chisel3.core.stop(0)
  }
}

class StopperTests(c: Stopper) extends PeekPokeTester(c) {
  for (i <- 0 until 4) {
    poke(c.io.in, i)
    step(1)
  }
}

object StopperMain {
  def main(args: Array[String]): Unit = {
    if (args.size > 0) {
      if (!Driver(() => new Stopper, "firrtl")(c => new StopperTests(c))) System.exit(1)
    } else {
      if (!Driver.run(() => new Stopper, "./test_run_dir/playground.Stopper/Stopper")(c => new StopperTests(c))) System.exit(1)
    }
  }
}
