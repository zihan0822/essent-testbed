package playground

import chisel3._
import chisel3.iotesters.{PeekPokeTester, Driver}


class Memo extends Module {
  val io = IO(new Bundle {
    val wen     = Input(Bool())
    val wrAddr  = Input(UInt(8.W))
    val wrData  = Input(UInt(8.W))
    val ren     = Input(Bool())
    val rdAddr  = Input(UInt(8.W))
    val rdData  = Output(UInt(8.W))
  })
  val mem = Mem(256, UInt(8.W))

  // --------------------------------------------------- \\
  // When wen is asserted, write wrData to mem at wrAddr 
  // When ren is asserted, rdData holds the output out of
  // reading the mem at rdAddr
  // --------------------------------------------------- \\

  // write
  when (io.wen) { mem(io.wrAddr) := io.wrData }
  
  // read
  io.rdData := 0.U
  when (io.ren) { io.rdData := mem(io.rdAddr) }

  // --------------------------------------------------- \\

}


class MemoTests(c: Memo) extends PeekPokeTester(c) {
  def rd(addr: Int, data: Int) = {
    poke(c.io.ren, 1)
    poke(c.io.rdAddr, addr)
    step(1)
    expect(c.io.rdData, data)
  }
  def wr(addr: Int, data: Int)  = {
    poke(c.io.wen,    1)
    poke(c.io.wrAddr, addr)
    poke(c.io.wrData, data)
    step(1)
  }
  wr(0, 1)
  rd(0, 1)
  wr(9, 11)
  rd(9, 11)
}



object MemoMain {
  def main(args: Array[String]): Unit = {
    if (args.size > 0) {
      if (!Driver(() => new Memo(), "firrtl")(c => new MemoTests(c))) System.exit(1)
    } else {
      if (!Driver.run(() => new Memo(), "./test_run_dir/playground.Memo/Memo")(c => new MemoTests(c))) System.exit(1)
    }
  }
}
