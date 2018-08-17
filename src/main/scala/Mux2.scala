// See LICENSE.txt for license details.
package essent.testbed

import chisel3._
import chisel3.iotesters.{PeekPokeTester, Driver}

class Mux2 extends Module {
  val io = IO(new Bundle {
    val sel = Input(UInt(1.W))
    val in0 = Input(UInt(1.W))
    val in1 = Input(UInt(1.W))
    val out = Output(UInt(1.W))
  })
  // io.out := (io.sel & io.in1) | (~io.sel & io.in0)
  io.out := Mux(io.sel(0), io.in1, io.in0)
}

class Mux2Tests(c: Mux2) extends PeekPokeTester(c) {
  step(1)
}

object Mux2Main {
  def main(args: Array[String]): Unit = {
    if (!Driver(() => new Mux2(), "verilator")(c => new Mux2Tests(c))) System.exit(1)
  }
}
