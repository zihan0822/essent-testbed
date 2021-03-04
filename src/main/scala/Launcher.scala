package essent.testbed

import chisel3._
import chisel3.iotesters._

object Launcher {
  val allTests = List("Accumulator", "Adder", "BigUInt", "Counter",
                      "DynamicMemorySearch", "Hello", "LFSR16", "Max2", "MaxN",
                      "Memo", "Mul", "Mux2", "Mux4", "Mux8", "SingleEvenFilter", "SoloReg", "SIntLit",
                      "VecShiftRegister", "VecShiftRegisterParam",
                      "VecShiftRegisterSimple", "VendingMachine",
                      "VendingMachineSwitch")
                      // "RealGCD", "RealGCD2",

  def testDUT(dutName: String): Boolean = dutName match {
    case "Accumulator" =>
      EssentBackend.buildAndRun(dutName)(() => new Accumulator())(c => new AccumulatorTests(c))
    case "Adder" =>
      EssentBackend.buildAndRun(dutName)(() => new Adder(16))(c => new AdderTests(c))
    case "BigUInt" =>
      EssentBackend.buildAndRun(dutName)(() => new BigUInt(128))(c => new BigUIntTests(c))
    case "Counter" =>
      EssentBackend.buildAndRun(dutName)(() => new Counter())(c => new CounterTests(c))
    case "DynamicMemorySearch" =>
      EssentBackend.buildAndRun(dutName)(() => new DynamicMemorySearch(32, 8))(c => new DynamicMemorySearchTests(c))
    case "Hello" =>
      EssentBackend.buildAndRun(dutName)(() => new Hello())(c => new HelloTests(c))
    case "LFSR16" =>
      EssentBackend.buildAndRun(dutName)(() => new LFSR16())(c => new LFSR16Tests(c))
    case "Max2" =>
      EssentBackend.buildAndRun(dutName)(() => new Max2())(c => new Max2Tests(c))
    case "MaxN" =>
      EssentBackend.buildAndRun(dutName)(() => new MaxN(2, 12))(c => new MaxNTests(c))
    case "Memo" =>
      EssentBackend.buildAndRun(dutName)(() => new Memo())(c => new MemoTests(c))
    case "Mul" =>
      EssentBackend.buildAndRun(dutName)(() => new Mul())(c => new MulTests(c))
    case "Mux2" =>
      EssentBackend.buildAndRun(dutName)(() => new Mux2())(c => new Mux2Tests(c))
    case "Mux4" =>
      EssentBackend.buildAndRun(dutName)(() => new Mux4())(c => new Mux4Tests(c))
    case "Mux8" =>
      EssentBackend.buildAndRun(dutName)(() => new Mux8())(c => new Mux8Tests(c))
    // case "RealGCD" =>
    //   EssentBackend.buildAndRun(dutName)(() => new RealGCD())(c => new RealGCDTests(c))
    // case "RealGCD2" =>
    //   EssentBackend.buildAndRun(dutName)(() => new RealGCD2())(c => new GCDPeekPokeTester(c))
    case "SingleEvenFilter" =>
      EssentBackend.buildAndRun(dutName)(() => new SingleEvenFilter(UInt(16.W)))(c => new SingleEvenFilterTests(c))
    case "SIntLit" =>
      EssentBackend.buildAndRun(dutName)(() => new SIntLit(16))(c => new SIntLitTests(c))
    case "SoloReg" =>
      EssentBackend.buildAndRun(dutName)(() => new SoloReg())(c => new SoloRegTests(c))
    case "Stopper" =>
      EssentBackend.buildAndRun(dutName)(() => new Stopper())(c => new StopperTests(c))
    case "VecShiftRegister" =>
      EssentBackend.buildAndRun(dutName)(() => new VecShiftRegister())(c => new VecShiftRegisterTests(c))
    case "VecShiftRegisterParam" =>
      EssentBackend.buildAndRun(dutName)(() => new VecShiftRegisterParam(8,8))(c => new VecShiftRegisterParamTests(c))
    case "VecShiftRegisterSimple" =>
      EssentBackend.buildAndRun(dutName)(() => new VecShiftRegisterSimple())(c => new VecShiftRegisterSimpleTests(c))
    case "VendingMachine" =>
      EssentBackend.buildAndRun(dutName)(() => new VendingMachine())(c => new VendingMachineTests(c))
    case "VendingMachineSwitch" =>
      EssentBackend.buildAndRun(dutName)(() => new VendingMachineSwitch())(c => new VendingMachineSwitchTests(c))
    case s: String => throw new Exception(s"Unknown testcase $s")
  }

  def main(args: Array[String]): Unit = {
    val argsList = args.toList
    if (argsList.isEmpty)
      throw new Exception("Please give name of testcase")
    val dutName = argsList.head
    if (dutName == "all") {
      if (!(allTests forall testDUT)) System.exit(1)
    } else {
      if (!testDUT(dutName)) System.exit(1)
    }
  }
}
