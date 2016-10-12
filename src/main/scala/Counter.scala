// See LICENSE.txt for license details.
package playground

import Chisel._
import Chisel.iotesters.{PeekPokeTester, Driver}

object Counter {
  def wrapAround(n: UInt, max: UInt) = 
    Mux(n > max, 0.U, n)

  // ---------------------------------------- \\
  // Modify this function to increment by the
  // amt only when en is asserted
  // ---------------------------------------- \\
  def counter(max: UInt, en: Bool, amt: UInt): UInt = {
    val x = Reg(init=UInt(0, max.getWidth))
    when (en) {
      x := wrapAround(x + amt, max)
    }
    x
  }
  // ---------------------------------------- \\

}

class Counter extends Module {
  val io = new Bundle {
    val inc = Bool(INPUT)
    val amt = UInt(INPUT,  4)
    val tot = UInt(OUTPUT, 8)
  }
  io.tot := Counter.counter(255.U, io.inc, io.amt)
}

class CounterTests(c: Counter) extends PeekPokeTester(c) {
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

object CounterMain {
  def main(args: Array[String]): Unit = {
    if (args.size > 0) {
      if (!Driver(() => new Counter(), "firrtl")(c => new CounterTests(c))) System.exit(1)
    } else {
      if (!Driver.run(() => new Counter(), "./test_run_dir/playground.Counter/Counter")(c => new CounterTests(c))) System.exit(1)
    }
  }
}