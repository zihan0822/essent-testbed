package essent.testbed

import chisel3._
import chisel3.util._
import chisel3.iotesters.{PeekPokeTester, Driver}
import scala.collection.mutable.ArrayBuffer


class Mul extends Module {
  val io = IO(new Bundle {
    val x   = Input(UInt(4.W))
    val y   = Input(UInt(4.W))
    val z   = Output(UInt(8.W))
  })
  val muls = new ArrayBuffer[UInt]()

  // -------------------------------- \\
  // Calculate io.z = io.x * io.y by
  // building filling out muls
  // -------------------------------- \\
  for {
    x <- 0 to 15
    y <- 0 to 15
  } muls += (x*y).U(8.W)

  val vec_mapper = VecInit(muls)
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
    if (!Driver(() => new Mul(), "firrtl")(c => new MulTests(c))) System.exit(1)
  }
}
