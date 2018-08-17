package essent.testbed

import chisel3._
import chisel3.iotesters.{PeekPokeTester, Driver}


class SIntLit(val w: Int) extends Module {
  val io = IO(new Bundle {
    val in = Input(SInt(w.W))
    val out = Output(SInt(w.W))
  })
  io.out := -2.S(w.W) + io.in
}

class SIntLitTests(c: SIntLit) extends PeekPokeTester(c) {
  for (i <- 2 until 10) {
    poke(c.io.in, i)
    step(1)
    expect(c.io.out, (i-2).S)
  }
}

object SIntLitMain {
  def main(args: Array[String]): Unit = {
    if (args.size > 0) {
      if (!Driver(() => new SIntLit(16), "firrtl")(c => new SIntLitTests(c))) System.exit(1)
    } else {
      if (!Driver.run(() => new SIntLit(16), "./test_run_dir/essent.testbed.SIntLit/SIntLit")(c => new SIntLitTests(c))) System.exit(1)
    }
  }
}
