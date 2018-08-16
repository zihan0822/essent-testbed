package playground

import chisel3._
import chisel3.util._
import chisel3.iotesters.{PeekPokeTester, Driver}


object GCDCalculator {
  def computeGcdResultsAndCycles(a: Int, b: Int, depth: Int = 1): (Int, Int) = {
    if(b == 0) {
      (a, depth)
    }
    else {
      computeGcdResultsAndCycles(b, a%b, depth+1 )
    }
  }
}

object RealGCD2 {
  val num_width = 16
}

class RealGCD2Input extends Bundle {
  val a = UInt(RealGCD2.num_width.W)
  val b = UInt(RealGCD2.num_width.W)
}

class RealGCD2 extends Module {
  val io  = IO(new Bundle {
    val in  = DeqIO(new RealGCDInput())
    val out = Output(Valid(UInt(16.W)))
  })

  val x = Reg(UInt(RealGCD2.num_width.W))
  val y = Reg(UInt(RealGCD2.num_width.W))
  val p = RegInit(false.B)

  val ti = RegInit(0.U(RealGCD2.num_width.W))
  ti := ti + UInt(1)

  io.in.ready := !p

  when (io.in.valid && !p) {
    x := io.in.bits.a
    y := io.in.bits.b
    p := true.B
  }

  when (p) {
    when (x > y)  { x := y; y := x }
      .otherwise    { y := y - x }
  }

  printf("ti %d  x %d y %d  in_ready %d  in_valid %d  out %d out_valid %d==============\n",
    ti, x, y, io.in.ready, io.in.valid, io.out.bits, io.out.valid)
  //      ti, x, y, io.in.ready, io.in.valid, io.out.bits, io.out.ready, io.out.valid)

  io.out.bits  := x
  io.out.valid := y === 0.U && p
  when (io.out.valid) {
    p := false.B
  }
}

class GCDPeekPokeTester(c: RealGCD2) extends PeekPokeTester(c)  {
  for {
    i <- 1 to 10
    j <- 1 to 10
  } {
    val (gcd_value, cycles) = GCDCalculator.computeGcdResultsAndCycles(i, j)

    poke(c.io.in.bits.a, i)
    poke(c.io.in.bits.b, j)
    poke(c.io.in.valid, 1)

    var count = 0
    while(peek(c.io.out.valid) == BigInt(0) && count < 20) {
      step(1)
      count += 1
    }
    if(count > 30) {
      println(s"Waited $count cycles on gcd inputs $i, $j, giving up")
      System.exit(0)
    }
    expect(c.io.out.bits, gcd_value)
    step(1)
  }
}

object GCDSpecMain {
  def main(args: Array[String]): Unit = {
    if (args.size > 0) {
      if (!Driver(() => new RealGCD2(), "firrtl")(c => new GCDPeekPokeTester(c))) System.exit(1)
    } else {
      if (!Driver.run(() => new RealGCD2(), "./test_run_dir/playground.RealGCD2/RealGCD2")(c => new GCDPeekPokeTester(c))) System.exit(1)
    }
  }
}

