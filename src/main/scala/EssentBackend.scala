package playground

import chisel3._
import chisel3.iotesters.{PeekPokeTester, Driver, copyVerilatorHeaderFiles}
import essent._

import java.io.{File, FileWriter}
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.{Files, Paths}

// FUTURE: consider moving this into essent repo/package

object EssentBackend {
  def buildAndRun[T <: chisel3.Module](dutGen: () => T)(testerGen: T => PeekPokeTester[T]) = {
    // emit firrtl
    val circuit = chisel3.Driver.elaborate(dutGen)
    // parse firrtl
    val chirrtl = firrtl.Parser.parse(chisel3.Driver.emit(circuit))
    val dut = (circuit.components find (_.name == circuit.name)).get.id.asInstanceOf[T]
    // make output directory
    val dir = new File(s"my_run_dir2/${dut.getClass.getName}")
    dir.mkdirs()
    val buildDir = dir.getAbsolutePath
    val dutName = chirrtl.main
    // emit .fir file for essent to read in
    val firFile = new File(buildDir, s"$dutName.fir")
    val firWriter = new FileWriter(firFile)
    firWriter.write(chirrtl.serialize)
    firWriter.close
    // copy over needed headers
    chisel3.iotesters.copyVerilatorHeaderFiles(buildDir)
    val commWrapResource = essent.Driver.getClass.getResourceAsStream("/comm_wrapper.h")
    val commWrapDestPath = Paths.get(buildDir + "/comm_wrapper.h")
    Files.copy(commWrapResource, commWrapDestPath, REPLACE_EXISTING)
    // generate cpp
    essent.Driver.generate(TestFlags(firFile))
    // compile cpp
    if (essent.Driver.compileCPP(dutName, buildDir).! != 0)
      throw new Exception("compile error!")
    // execute the dut
    chisel3.iotesters.Driver.run(dutGen, s"$buildDir/$dutName")(testerGen)
  }
}
