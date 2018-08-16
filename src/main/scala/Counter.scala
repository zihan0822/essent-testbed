// See LICENSE.txt for license details.
package playground

import chisel3._
import chisel3.iotesters.{PeekPokeTester, Driver}

object Counter {
  def wrapAround(n: UInt, max: UInt) = 
    Mux(n > max, 0.U, n)

  // ---------------------------------------- \\
  // Modify this function to increment by the
  // amt only when en is asserted
  // ---------------------------------------- \\
  def counter(max: UInt, en: Bool, amt: UInt): UInt = {
    val x = RegInit(max.cloneType, 0.U)
    when (en) {
      x := wrapAround(x + amt, max)
    }
    x
  }
  // ---------------------------------------- \\

}

class Counter extends Module {
  val io = IO(new Bundle {
    val inc = Input(Bool())
    val amt = Input(UInt(4.W))
    val tot = Output(UInt(8.W))
  })
  io.tot := Counter.counter(255.U, io.inc, io.amt)
}

class MyCounterTest(c: Counter) extends PeekPokeTester(c) {
  step(1)
  poke(c.io.inc, 1)
	poke(c.io.amt, 4)
	step(1)
	peek(c.io.tot)
	step(1)
	peek(c.io.tot)
	step(1)
	peek(c.io.tot)
  poke(c.io.inc, 0)
	step(1)
	peek(c.io.tot)
}

class CounterTests(c: Counter) extends PeekPokeTester(c) {
  val maxInt  = 16
  var curCnt  = 0

  def intWrapAround(n: Int, max: Int) = 
    if(n > max) 0 else n

  // let it spin for a bit
  for (i <- 0 until 5) {
    step(1)
  }

  for (i <- 0 until 10) {
    val inc = rnd.nextBoolean()
    val amt = rnd.nextInt(maxInt)
    poke(c.io.inc, if (inc) 1 else 0)
    poke(c.io.amt, amt)
    step(1)
    curCnt = if(inc) intWrapAround(curCnt + amt, 255) else curCnt
    expect(c.io.tot, curCnt)
  }
}


object CounterMain {
  def main(args: Array[String]): Unit = {
    if (args.size > 0) {
      if (!Driver(() => new Counter(), "firrtl")(c => new CounterTests(c))) System.exit(1)
    } else {
      if (!Driver.run(() => new Counter(), "./test_run_dir/playground.Counter/Counter")(c => new CounterTests(c))) System.exit(1)
    }
  }
}