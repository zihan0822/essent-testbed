package essent.testbed

import chisel3._
import chisel3.iotesters.{PeekPokeTester, Driver}


class VecShiftRegisterParam(val n: Int, val w: Int) extends Module {
  val io = IO(new Bundle {
    val in  = Input(UInt(w.W))
    val out = Output(UInt(w.W))
  })
  val initValues = Seq.fill(n) { 0.U(w.W) }
  val delays = RegInit(VecInit(initValues))
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
    if (!Driver(() => new VecShiftRegisterParam(8,8), "firrtl")(c => new VecShiftRegisterParamTests(c))) System.exit(1)
  }
}
