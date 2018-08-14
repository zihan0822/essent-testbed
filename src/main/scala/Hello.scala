// See LICENSE.txt for license details.
package playground

import chisel3._
import chisel3.iotesters.{PeekPokeTester, Driver}

class Hello extends Module {
  val io = IO(new Bundle {})
  printf("hello\n")
}

class HelloTests(c: Hello) extends PeekPokeTester(c) {
  step(1)
}

object HelloMain {
  def main(args: Array[String]): Unit = {
    if (args.size > 0) {
      if (!Driver(() => new Hello(), "firrtl")(c => new HelloTests(c))) System.exit(1)
    } else {
      if (!Driver.run(() => new Hello(), "./test_run_dir/playground.Hello/Hello")(c => new HelloTests(c))) System.exit(1)
    }
  }
}
