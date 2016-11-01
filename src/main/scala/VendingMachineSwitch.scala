package playground

import chisel3._
import chisel3.util._
import Chisel.iotesters.{PeekPokeTester, Driver}


class VendingMachineSwitch extends Module {
  val io = IO(new Bundle {
    val nickel = Input(Bool())
    val dime   = Input(Bool())
    val valid  = Output(Bool())
  })
  val s_idle :: s_5 :: s_10 :: s_15 :: s_ok :: Nil = Enum(UInt(), 5)
  val state = Reg(init = s_idle)
  
  switch (state) {
    is (s_idle) {
      when (io.nickel) { state := s_5 }
      when (io.dime) { state := s_10 }
    }
    is (s_5) {
      when (io.nickel) { state := s_10 }
      when (io.dime) { state := s_15 }
    }
    is (s_10) {
      when (io.nickel) { state := s_15 }
      when (io.dime) { state := s_ok }
    }
    is (s_15) {
      when (io.nickel) { state := s_ok }
      when (io.dime) { state := s_ok }
    }
    is (s_ok) {
      state := s_idle
    }
  }
  io.valid := (state ===s_ok)
}

class VendingMachineSwitchTests(c: VendingMachineSwitch) extends PeekPokeTester(c) {
  var money = 0
  var isValid = false
  for (t <- 0 until 20) {
    val coin     = rnd.nextInt(3)*5
    val isNickel = coin == 5
    val isDime   = coin == 10

    // Advance circuit
    poke(c.io.nickel, if (isNickel) 1 else 0)
    poke(c.io.dime,   if (isDime) 1 else 0)
    step(1)

    // Advance model
    money = if (isValid) 0 else (money + coin)
    isValid = money >= 20

    // Compare
    expect(c.io.valid, if (isValid) 1 else 0)
  }
}


object VendingMachineSwitchMain {
  def main(args: Array[String]): Unit = {
    if (args.size > 0) {
      if (!Driver(() => new VendingMachineSwitch(), "firrtl")(c => new VendingMachineSwitchTests(c))) System.exit(1)
    } else {
      if (!Driver.run(() => new VendingMachineSwitch(), "./test_run_dir/playground.VendingMachineSwitch/VendingMachineSwitch")(c => new VendingMachineSwitchTests(c))) System.exit(1)
    }
  }
}
