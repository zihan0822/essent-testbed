package playground

import chisel3._
import chisel3.util._
import Chisel.iotesters.{PeekPokeTester, Driver}

import scala.math.BigInt
import scala.util.Random

class BigUInt(val w: Int) extends Module {
  val shamt = 10
  val io = IO(new Bundle {
    val inA = Input(UInt(width = w))
    val inB = Input(UInt(width = w))
    val inShamt = Input(UInt(width = 4))
    val outPassA = Output(UInt(width = w))
    val outAPlusB = Output(UInt(width = w))
    // val outASubB = Output(SInt(width = w+1))
    val outAMulB = Output(UInt(width = w+w))
    // val outADivB = Output(UInt(width = w))
    // val outARemB = Output(UInt(width = w))
    val outALtB = Output(UInt(width = 1))
    val outALeqB = Output(UInt(width = 1))
    val outAGtB = Output(UInt(width = 1))
    val outAGeqB = Output(UInt(width = 1))
    val outAEqB = Output(UInt(width = 1))
    val outANeqB = Output(UInt(width = 1))
    val outAShl = Output(UInt(width = w+shamt))
    val outAShr = Output(UInt(width = w-shamt))
    val outADshl = Output(UInt(width = w+shamt))
    val outADshr = Output(UInt(width = w-shamt))
    // val outNegA = Output(SInt(width = w+1))
    // val outNotA = Output(UInt(width = w))
    val outAAndB = Output(UInt(width = w))
    val outAOrB = Output(UInt(width = w))
    val outAXorB = Output(UInt(width = w))
    val outACatB = Output(UInt(width = w+w))
  })
  io.outPassA := io.inA
  io.outAPlusB := io.inA + io.inB
  // io.outASubB := io.inA.asSInt - io.inB.asSInt
  io.outAMulB := io.inA * io.inB
  // io.outADivB := io.inA / io.inB
  // io.outARemB := io.inA % io.inB
  io.outALtB := io.inA < io.inB
  io.outALeqB := io.inA <= io.inB
  io.outAGtB := io.inA > io.inB
  io.outAGeqB := io.inA >= io.inB
  io.outAEqB := io.inA === io.inB
  io.outANeqB := io.inA =/= io.inB
  io.outAShl := io.inA << shamt
  io.outAShr := io.inA >> shamt
  io.outADshl := io.inA << io.inShamt
  io.outADshr := io.inA >> io.inShamt
  // io.outNegA := -io.inA
  // io.outNotA := ~io.inA
  io.outAAndB := io.inA & io.inB
  io.outAOrB := io.inA | io.inB
  io.outAXorB := io.inA ^ io.inB
  io.outACatB := Cat(io.inA, io.inB)
}

class BigUIntTests(c: BigUInt) extends PeekPokeTester(c) {
  for (i <- 0 until 4) {
    val a = BigInt(c.w, Random)
    val b = BigInt(c.w, Random)
    val mask = (BigInt(1) << c.w) - 1
    poke(c.io.inA, a)
    poke(c.io.inB, b)
    poke(c.io.inShamt, c.shamt)
    expect(c.io.outPassA, a)
    expect(c.io.outAPlusB, (a + b) & mask)
    // expect(c.io.outASubB, a - b)
    expect(c.io.outAMulB, a * b)
    // expect(c.io.outADivB, a / b)
    // expect(c.io.outARemB, a % b)
    expect(c.io.outALtB, a < b)
    expect(c.io.outALeqB, a <= b)
    expect(c.io.outAGtB, a > b)
    expect(c.io.outAGeqB, a >= b)
    expect(c.io.outAEqB, a == b)
    expect(c.io.outANeqB, a != b)
    expect(c.io.outAShl, a << c.shamt)
    expect(c.io.outAShr, a >> c.shamt)
    expect(c.io.outADshl, a << c.shamt)
    expect(c.io.outADshr, a >> c.shamt)
    // expect(c.io.outNegA, -a)
    // expect(c.io.outNotA, ~a)
    expect(c.io.outAAndB, a & b)
    expect(c.io.outAOrB, a | b)
    expect(c.io.outAXorB, a ^ b)
    expect(c.io.outACatB, (a << c.w) | b)
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
