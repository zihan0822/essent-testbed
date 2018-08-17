package essent.testbed

import chisel3._
import chisel3.iotesters.{PeekPokeTester, Driver}


class Max2 extends Module {
  val io = IO(new Bundle {
    val in0 = Input(UInt(8.W))
    val in1 = Input(UInt(8.W))
    val out = Output(UInt(8.W))
  })
  io.out := Mux(io.in0 > io.in1, io.in0, io.in1)
}

class Max2Tests(c: Max2) extends PeekPokeTester(c) {
  for (i <- 0 until 10) {
    // FILL THIS IN HERE
    val in0 = rnd.nextInt(256)
    val in1 = rnd.nextInt(256)
    poke(c.io.in0, in0)
    poke(c.io.in1, in1)
    // FILL THIS IN HERE
    step(1)
    expect(c.io.out, if (in0 > in1) in0 else in1)
  }
}


object Max2Main {
  def main(args: Array[String]): Unit = {
    if (args.size > 0) {
      if (!Driver(() => new Max2(), "firrtl")(c => new Max2Tests(c))) System.exit(1)
    } else {
      if (!Driver.run(() => new Max2(), "./test_run_dir/essent.testbed.Max2/Max2")(c => new Max2Tests(c))) System.exit(1)
    }
  }
}
