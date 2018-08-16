package playground

import chisel3._
import chisel3.util._
import chisel3.iotesters.{PeekPokeTester, Driver}

class RealGCDInput extends Bundle {
  val a = UInt(16.W)
  val b = UInt(16.W)
}

class RealGCD extends Module {
  val io  = IO(new Bundle {
    val in  = DeqIO(new RealGCDInput())
    val out = Output(Valid(UInt(16.W)))
  })

  val rA = Reg(UInt())
  val rB = RegInit(0.U)
  val busy = RegInit(false.B)

  when (io.in.valid) {
    rA := io.in.bits.a
    rB := io.in.bits.b
    busy := true.B
  }

  io.out.valid := false.B
  when (rB =/= 0.U) {
    when (rA > rB) {
      rA := rB
      rB := rA - rB
    } .elsewhen (rA === rB) {
      io.out.valid := true.B
      busy := false.B
      rB := 0.U
    } .otherwise {
      rA := rB
      rB := rA
    }
  }

  io.out.bits := rA
  io.in.ready := !busy
}


class RealGCDTests(c: RealGCD) extends PeekPokeTester(c) {
  val inputs = List( (48, 32), (7, 3), (100, 10) )
  val outputs = List( 16, 1, 10)

  var i = 0
  do {
    var transfer = false
    do {
      poke(c.io.in.bits.a, inputs(i)._1)
      poke(c.io.in.bits.b, inputs(i)._2)
      poke(c.io.in.valid,  1)
      transfer = (peek(c.io.in.ready) == 1)
      step(1)
    } while (t < 100 && !transfer)

    do {
      poke(c.io.in.valid, 0)
      step(1)
    } while (t < 100 && (peek(c.io.out.valid) == 0))

    expect(c.io.out.bits, outputs(i))
    i += 1;
  } while (t < 100 && i < 3)
  if (t >= 100) fail
}


object RealGCDMain {
  def main(args: Array[String]): Unit = {
    if (args.size > 0) {
      if (!Driver(() => new RealGCD(), "firrtl")(c => new RealGCDTests(c))) System.exit(1)
    } else {
      if (!Driver.run(() => new RealGCD(), "./test_run_dir/playground.RealGCD/RealGCD")(c => new RealGCDTests(c))) System.exit(1)
    }
  }
}
