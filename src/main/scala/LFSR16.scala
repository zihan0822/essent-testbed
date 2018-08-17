package essent.testbed

import chisel3._
import chisel3.util._
import chisel3.iotesters.{PeekPokeTester, Driver}


class LFSR16 extends Module {
  val io = IO(new Bundle {
    val inc = Input(Bool())
    val out = Output(UInt(16.W))
  })
  val res = RegInit(1.U(16.W))
  when (io.inc) { 
    val nxt_res = Cat(res(0)^res(2)^res(3)^res(5), res(15,1)) 
    res := nxt_res
  }
  io.out := res
}

class LFSR16Tests(c: LFSR16) extends PeekPokeTester(c) {
  var res = 1
  for (t <- 0 until 16) {
    val inc = rnd.nextInt(2)
    poke(c.io.inc, inc)
    step(1)
    if (inc == 1) {
      val bit = ((res >> 0) ^ (res >> 2) ^ (res >> 3) ^ (res >> 5) ) & 1;
      res = (res >> 1) | (bit << 15);
    }
    expect(c.io.out, res)
  }
}

object LFSR16Main {
  def main(args: Array[String]): Unit = {
    if (!Driver(() => new LFSR16(), "firrtl")(c => new LFSR16Tests(c))) System.exit(1)
  }
}
