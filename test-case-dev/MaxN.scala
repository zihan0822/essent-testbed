package playground

import chisel3._
import Chisel.iotesters.{PeekPokeTester, Driver}


class MaxN(val n: Int, val w: Int) extends Module {

  private def MaxN(x: UInt, y: UInt) = Mux(x > y, x, y)

  val io = IO(new Bundle {
    val ins = Input(Vec(n, UInt(width = w)))
    val out = Output(UInt(width = w))
  })
  io.out := io.ins.reduceLeft(MaxN)
}

class MaxNTests(c: MaxN) extends PeekPokeTester(c) {
  for (i <- 0 until 10) {
    // FILL THIS IN HERE
    val in0 = rnd.nextInt(256)
    val in1 = rnd.nextInt(256)
    poke(c.io.ins(0), in0)
    poke(c.io.ins(1), in1)
    // FILL THIS IN HERE
    step(1)
    expect(c.io.out, if (in0 > in1) in0 else in1)
  }
}

object MaxNMain {
  def main(args: Array[String]): Unit = {
    if (args.size > 0) {
      if (!Driver(() => new MaxN(2, 12), "firrtl")(c => new MaxNTests(c))) System.exit(1)
    } else {
      if (!Driver.run(() => new MaxN(2, 12), "./test_run_dir/playground.MaxN/MaxN")(c => new MaxNTests(c))) System.exit(1)
    }
  }
}
