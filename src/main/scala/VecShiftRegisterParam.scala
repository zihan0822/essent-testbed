package playground

import Chisel._
import Chisel.iotesters.{PeekPokeTester, Driver}


class VecShiftRegisterParam(val n: Int, val w: Int) extends Module {
  val io = new Bundle {
    val in  = UInt(INPUT,  w)
    val out = UInt(OUTPUT, w)
  }
  val delays = Reg(init = Vec.fill(n)(UInt(0, width = w)))
  for (i <- n-1 to 1 by -1)
    delays(i) := delays(i-1) 
  delays(0) := io.in
  io.out := delays(n-1)
}

class VecShiftRegisterParamTests(c: VecShiftRegisterParam) extends PeekPokeTester(c) {
  val reg = Array.fill(c.n){ 0 }
  for (t <- 0 until 16) {
    val in = rnd.nextInt(1 << c.w)
    poke(c.io.in, in)
    step(1)
    for (i <- c.n-1 to 1 by -1)
      reg(i) = reg(i-1)
    reg(0) = in
    expect(c.io.out, reg(c.n-1))
  }
}

object VecShiftRegisterParamMain {
  def main(args: Array[String]): Unit = {
    if (args.size > 0) {
      if (!Driver(() => new VecShiftRegisterParam(8,8), "firrtl")(c => new VecShiftRegisterParamTests(c))) System.exit(1)
    } else {
      if (!Driver.run(() => new VecShiftRegisterParam(8,8), "./test_run_dir/playground.VecShiftRegisterParam/VecShiftRegisterParam")(c => new VecShiftRegisterParamTests(c))) System.exit(1)
    }
  }
}
