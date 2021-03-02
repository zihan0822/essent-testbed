package essent.testbed

import chisel3._
import chisel3.iotesters.{PeekPokeTester, Driver}


class Stopper extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(2.W))
  })
  when (io.in === 2.U) {
    chisel3.stop(4)
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
    if (!Driver(() => new Stopper, "firrtl")(c => new StopperTests(c))) System.exit(1)
  }
}
