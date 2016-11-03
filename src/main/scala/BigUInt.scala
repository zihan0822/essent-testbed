package playground

import chisel3._
import chisel3.util._
import Chisel.iotesters.{PeekPokeTester, Driver}

import scala.math.BigInt
import scala.util.Random

class BigUInt(val w: Int) extends Module {
  val io = IO(new Bundle {
    val inA = Input(UInt(width = w))
    val inB = Input(UInt(width = w))
    val outPassA = Output(UInt(width = w))
    val outACatB = Output(UInt(width = w+w))
    val outAPlusB = Output(UInt(width = w+1))
  })
  io.outPassA := io.inA
  io.outACatB := Cat(io.inA, io.inB)
  io.outAPlusB := io.inA + io.inB
}

class BigUIntTests(c: BigUInt) extends PeekPokeTester(c) {
  for (i <- 0 until 4) {
    val a = BigInt(c.w, Random)
    val b = BigInt(c.w, Random)
    poke(c.io.inA, a)
    poke(c.io.inB, b)
    expect(c.io.outPassA, a)
    expect(c.io.outACatB, (a << c.w) | b)
    expect(c.io.outAPlusB, a + b)
    step(1)
  }
}

object BigUIntMain {
  def main(args: Array[String]): Unit = {
    if (args.size > 0) {
      if (!Driver(() => new BigUInt(128), "firrtl")(c => new BigUIntTests(c))) System.exit(1)
    } else {
      if (!Driver.run(() => new BigUInt(128), "./test_run_dir/playground.BitUInt/BitUInt")(c => new BigUIntTests(c))) System.exit(1)
    }
  }
}
