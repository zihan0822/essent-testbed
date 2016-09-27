// See LICENSE.txt for license details.
package playground

import Chisel._
import Chisel.iotesters.{PeekPokeTester, Driver}

class Mux2 extends Module {
  val io = new Bundle {
    val sel = UInt(INPUT,  1)
    val in0 = UInt(INPUT,  1)
    val in1 = UInt(INPUT,  1)
    val out = UInt(OUTPUT, 1)
  }
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
