package playground

import chisel3._
import chisel3.util._
import chisel3.iotesters.{PeekPokeTester, Driver}


abstract class Filter[T <: Data](dtype: T) extends Module {
  val io = IO(new Bundle {
    val in = Input(Valid(dtype))
    val out = Output(Valid(dtype))
  })
}

class PredicateFilter[T <: Data](dtype: T, f: T => Bool) extends Filter(dtype) {
  io.out.valid := io.in.valid && f(io.in.bits)
  io.out.bits  := io.in.bits
}

object SingleFilter {
  def apply[T <: UInt](dtype: T) = 
    Module(new PredicateFilter(dtype, (x: T) => x <= 9.U))
}

object EvenFilter {
  def apply[T <: UInt](dtype: T) = 
    Module(new PredicateFilter(dtype, (x: T) => x(0).toBool))
}

class SingleEvenFilter[T <: UInt](dtype: T) extends Filter(dtype) {
  val single = SingleFilter(dtype)
  val even   = EvenFilter(dtype)
  single.io.in  := io.in
  even.io.in    := single.io.out
  io.out        := even.io.out
}


class SingleEvenFilterTests[T <: UInt](c: SingleEvenFilter[T]) extends PeekPokeTester(c) {
  val maxInt  = 1 << 16
  for (i <- 0 until 10) {
    val in = rnd.nextInt(maxInt)
    poke(c.io.in.valid, 1)
    poke(c.io.in.bits, in)
    val isSingleEven = (in <= 9) && (in%2 == 1)
    step(1)
    expect(c.io.out.valid, if (isSingleEven) 1 else 0)
    expect(c.io.out.bits, in)
  }
}


object SingleEvenFilterMain {
  def main(args: Array[String]): Unit = {
    if (args.size > 0) {
      if (!Driver(() => new SingleEvenFilter(UInt(16.W)), "firrtl")(c => new SingleEvenFilterTests(c))) System.exit(1)
    } else {
      if (!Driver.run(() => new SingleEvenFilter(UInt(16.W)), "./test_run_dir/playground.SingleEvenFilter/SingleEvenFilter")(c => new SingleEvenFilterTests(c))) System.exit(1)
    }
  }
}