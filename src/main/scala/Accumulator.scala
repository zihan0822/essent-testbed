package playground

import Chisel._
import Chisel.iotesters.{PeekPokeTester, Driver}

class Accumulator extends Module {
  val io = new Bundle {
    val in  = UInt(width = 1, dir = INPUT)
    val out = UInt(width = 8, dir = OUTPUT)
  }
  val accumulator = Reg(init=UInt(0, 8))
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
    if (args.size > 0) {
      if (!Driver(() => new Accumulator(), "firrtl")(c => new AccumulatorTests(c))) System.exit(1)
    } else {
      if (!Driver.run(() => new Accumulator(), "./test_run_dir/playground.Accumulator/Accumulator")(c => new AccumulatorTests(c))) System.exit(1)
    }
  }
}
