package playground

import chisel3._
import Chisel.iotesters.{PeekPokeTester, Driver}


class VecShiftRegisterSimple extends Module {
  val io = IO(new Bundle {
    val in  = Input(UInt(width = 8))
    val out = Output(UInt(width = 8))
  })
  val delays = Reg(init = Vec.fill(4)(UInt(0, width = 8)))
  delays(0) := io.in
  delays(1) := delays(0)
  delays(2) := delays(1)
  delays(3) := delays(2)
  io.out    := delays(3)
}

class VecShiftRegisterSimpleTests(c: VecShiftRegisterSimple) extends PeekPokeTester(c) {
  val reg = Array.fill(4){ 0 }
  for (t <- 0 until 16) {
    val in = rnd.nextInt(256)
    poke(c.io.in, in)
    step(1)
    for (i <- 3 to 1 by -1)
      reg(i) = reg(i-1)
    reg(0) = in
    expect(c.io.out, reg(3))
  }
}

object VecShiftRegisterSimpleMain {
  def main(args: Array[String]): Unit = {
    if (args.size > 0) {
      if (!Driver(() => new VecShiftRegisterSimple(), "firrtl")(c => new VecShiftRegisterSimpleTests(c))) System.exit(1)
    } else {
      if (!Driver.run(() => new VecShiftRegisterSimple(), "./test_run_dir/playground.VecShiftRegisterSimple/VecShiftRegisterSimple")(c => new VecShiftRegisterSimpleTests(c))) System.exit(1)
    }
  }
}
