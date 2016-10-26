package playground

import chisel3._
import Chisel.iotesters.{PeekPokeTester, Driver}
import essent._

import java.io.File

object EssentBackend {
  def buildAndRun[T <: chisel3.Module](dutGen: () => T)(testerGen: T => PeekPokeTester[T]) = {
    // emit firrtl
    val circuit = chisel3.Driver.elaborate(dutGen)
    // parse firrtl
    val chirrtl = firrtl.Parser.parse(chisel3.Driver.emit(circuit))
    val dut = (circuit.components find (_.name == circuit.name)).get.id.asInstanceOf[T]
    // make output directory
    val dir = new File(s"my_run_dir/${dut.getClass.getName}"); dir.mkdirs()
    val buildDir = dir.getAbsolutePath
    val dutName = chirrtl.main
    // generate cpp
    essent.Driver.generate(chirrtl, buildDir)
    // compile cpp
    if (essent.Driver.compileCPP(dutName, buildDir).! != 0)
      throw new Exception("compile error!")
    // call into cpp
    Chisel.iotesters.Driver.run(dutGen, s"$buildDir/$dutName")(testerGen)
  }
}
