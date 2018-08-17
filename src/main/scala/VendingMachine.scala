package essent.testbed

import chisel3._
import chisel3.util.Enum
import chisel3.iotesters.{PeekPokeTester, Driver}


class VendingMachine extends Module {
  val io = IO(new Bundle {
    val nickel = Input(Bool())
    val dime   = Input(Bool())
    val valid  = Output(Bool())
  })
  val sIdle :: s5 :: s10 :: s15 :: sOk :: Nil = Enum(5)
  val state = RegInit(sIdle)

  // flush it out ...
  when(state === sIdle) {
    when (io.nickel) { state := s5 }
    when (io.dime) { state := s10 }
  } .elsewhen(state === s5) {
    when (io.nickel) { state := s10 }
    when (io.dime) { state := s15 }
  } .elsewhen(state === s10) {
    when (io.nickel) { state := s15 }
    when (io.dime) { state := sOk }
  } .elsewhen(state === s15) {
    when (io.nickel) { state := sOk }
    when (io.dime) { state := sOk }
  } .elsewhen(state === sOk) {
    state := sIdle
  }

  io.valid := (state === sOk)
}


class VendingMachineTests(c: VendingMachine) extends PeekPokeTester(c) {
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


object VendingMachineMain {
  def main(args: Array[String]): Unit = {
    if (args.size > 0) {
      if (!Driver(() => new VendingMachine(), "firrtl")(c => new VendingMachineTests(c))) System.exit(1)
    } else {
      if (!Driver.run(() => new VendingMachine(), "./test_run_dir/essent.testbed.VendingMachine/VendingMachine")(c => new VendingMachineTests(c))) System.exit(1)
    }
  }
}
