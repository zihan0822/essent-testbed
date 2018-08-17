package essent.testbed

import chisel3._
import chisel3.iotesters.{PeekPokeTester, Driver}


class Mux8 extends Module {
  val io = IO(new Bundle {
    val in   = Input(Vec(8, UInt(1.W)))
    val sel = Input(UInt(3.W))
    val out = Output(UInt(1.W))
  })

  val m0 = Module(new Mux4())
  m0.io.sel := io.sel(1,0)
  m0.io.in0 := io.in(0)
  m0.io.in1 := io.in(1)
  m0.io.in2 := io.in(2)
  m0.io.in3 := io.in(3)

  val m1 = Module(new Mux4())
  m1.io.sel := io.sel(1,0)
  m1.io.in0 := io.in(4)
  m1.io.in1 := io.in(5)
  m1.io.in2 := io.in(6)
  m1.io.in3 := io.in(7)

  val m2 = Module(new Mux2())
  m2.io.sel := io.sel(2)
  m2.io.in0 := m0.io.out
  m2.io.in1 := m1.io.out

  io.out := m2.io.out
}


class Mux8Tests(c: Mux8) extends PeekPokeTester(c) {
  for (sel <- 0 until 8) {
    for (in <- 0 until 255) {
      poke(c.io.sel, sel)
      for (i <- 0 until 8) {
        poke(c.io.in(i), (in >> i) & 1)
      }
      step(1)
      expect(c.io.out, (in >> sel) & 1)
    }
  }
}


object Mux8Main {
  def main(args: Array[String]): Unit = {
    if (args.size > 0) {
      if (!Driver(() => new Mux8(), "firrtl")(c => new Mux8Tests(c))) System.exit(1)
    } else {
      if (!Driver.run(() => new Mux8(), "./test_run_dir/essent.testbed.Mux8/Mux8")(c => new Mux8Tests(c))) System.exit(1)
    }
  }
}
