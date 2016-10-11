// See LICENSE.txt for license details.
package playground

import Chisel._
import Chisel.iotesters.{PeekPokeTester, Driver}


class SoloReg extends Module {
  val io = new Bundle {
    val en = Bool(INPUT)
    val in = UInt(INPUT, 8)
    val out = UInt(OUTPUT, 8)
  }
  val r = Reg(init=UInt(0))
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
      if (!Driver.run(() => new SoloReg(), "./test_run_dir/playground.SoloReg/SoloReg")(c => new SoloRegTests(c))) System.exit(1)
    }
  }
}
