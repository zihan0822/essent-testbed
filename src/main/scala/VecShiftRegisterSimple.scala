package playground

import chisel3._
import chisel3.iotesters.{PeekPokeTester, Driver}


class VecShiftRegisterSimple extends Module {
  val io = IO(new Bundle {
    val in  = Input(UInt(8.W))
    val out = Output(UInt(8.W))
  })

  val initValues = Seq.fill(4) { 0.U(8.W) }
  val delays = RegInit(VecInit(initValues))

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
