// See LICENSE.txt for license details.
package essent.testbed

import chisel3._
import chisel3.iotesters.{PeekPokeTester, Driver}


class SoloReg extends Module {
  val io = IO(new Bundle {
    val en = Input(Bool())
    val in = Input(UInt(8.W))
    val out = Output(UInt(8.W))
  })
  val r = RegInit(0.U)
  when(io.en) { r := io.in }
  io.out := r
}

class SoloRegTests(c: SoloReg) extends PeekPokeTester(c) {
  step(1)
  poke(c.io.in, 4)
  poke(c.io.en, 1)
  peek(c.io.out)
  peek(c.io.out)
  step(1)
  peek(c.io.out)
  step(1)
  poke(c.io.en, 0)
  poke(c.io.in, 2)
  step(1)
  peek(c.io.out)
  step(1)
  poke(c.io.en, 1)
  peek(c.io.out)
  step(1)
  peek(c.io.out)
}

object SoloRegMain {
  def main(args: Array[String]): Unit = {
    if (args.size > 0) {
      if (!Driver(() => new SoloReg(), "firrtl")(c => new SoloRegTests(c))) System.exit(1)
    } else {
      if (!Driver.run(() => new SoloReg(), "./test_run_dir/essent.testbed.SoloReg/SoloReg")(c => new SoloRegTests(c))) System.exit(1)
    }
  }
}
