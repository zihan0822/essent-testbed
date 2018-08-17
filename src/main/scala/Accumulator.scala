package essent.testbed

import chisel3._
import chisel3.iotesters.{PeekPokeTester, Driver}

class Accumulator extends Module {
  val io = IO(new Bundle {
    val in  = Input(UInt(1.W))
    val out = Output(UInt(8.W))
  })
  val accumulator = RegInit(UInt(8.W), 0.U)
  accumulator := accumulator + io.in
  io.out := accumulator
}

class AccumulatorTests(c: Accumulator) extends PeekPokeTester(c) {
  var tot = 0
  for (t <- 0 until 16) {
    val in = rnd.nextInt(2)
    poke(c.io.in, in)
    step(1)
    if (in == 1) tot += 1
    expect(c.io.out, tot)
  }
}

object AccumulatorMain {
  def main(args: Array[String]): Unit = {
    if (!Driver(() => new Accumulator(), "firrtl")(c => new AccumulatorTests(c))) System.exit(1)
  }
}
