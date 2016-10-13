package playground

import Chisel._
import Chisel.iotesters.{PeekPokeTester, Driver}
import scala.collection.mutable.ArrayBuffer


class Mul extends Module {
  val io = new Bundle {
    val x   = UInt(INPUT,  4)
    val y   = UInt(INPUT,  4)
    val z   = UInt(OUTPUT, 8)
  }
  val muls = new ArrayBuffer[UInt]()

  // -------------------------------- \\
  // Calculate io.z = io.x * io.y by
  // building filling out muls
  // -------------------------------- \\
  for {
    x <- 0 to 15
    y <- 0 to 15
  } muls += UInt(x*y, width=8)

  val vec_mapper = Vec(muls)
  io.z := vec_mapper((Cat(io.x, io.y)))
  // -------------------------------- \\
}

class MulTests(c: Mul) extends PeekPokeTester(c) {
  val maxInt  = 1 << 4
  for (i <- 0 until 10) {
    val x = rnd.nextInt(maxInt)
    val y = rnd.nextInt(maxInt)
    poke(c.io.x, x)
    poke(c.io.y, y)
    step(1)
    expect(c.io.z, (x * y))
  }
}


object MulMain {
  def main(args: Array[String]): Unit = {
    if (args.size > 0) {
      if (!Driver(() => new Mul(), "firrtl")(c => new MulTests(c))) System.exit(1)
    } else {
      if (!Driver.run(() => new Mul(), "./test_run_dir/playground.Mul/Mul")(c => new MulTests(c))) System.exit(1)
    }
  }
}
