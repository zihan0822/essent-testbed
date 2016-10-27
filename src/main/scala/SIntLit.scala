package playground

import Chisel._
import Chisel.iotesters.{PeekPokeTester, Driver}


class SIntLit(val w: Int) extends Module {
  val io = new Bundle {
    val in = SInt(INPUT,  w)
    val out = SInt(OUTPUT, w)
  }
  io.out := SInt(-2, width=w) + io.in
}

class SIntLitTests(c: SIntLit) extends PeekPokeTester(c) {
  for (i <- 2 until 10) {
    poke(c.io.in, i)
    step(1)
    expect(c.io.out, SInt(i-2))
  }
}

object SIntLitMain {
  def main(args: Array[String]): Unit = {
    if (args.size > 0) {
      if (!Driver(() => new SIntLit(16), "firrtl")(c => new SIntLitTests(c))) System.exit(1)
    } else {
      if (!Driver.run(() => new SIntLit(16), "./test_run_dir/playground.SIntLit/SIntLit")(c => new SIntLitTests(c))) System.exit(1)
    }
  }
}
