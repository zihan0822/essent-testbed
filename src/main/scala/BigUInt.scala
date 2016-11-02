package playground

import chisel3._
import Chisel.iotesters.{PeekPokeTester, Driver}

import scala.math.BigInt
import scala.util.Random

class BigUInt(val w: Int) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(width = w))
    val out = Output(UInt(width = w))
  })
  io.out := io.in
}

class BigUIntTests(c: BigUInt) extends PeekPokeTester(c) {
  for (i <- 0 until 4) {
    val a = BigInt(c.w, Random)
    poke(c.io.in, a)
    expect(c.io.out, a)
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
