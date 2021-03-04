package essent.testbed

import chisel3._
import chisel3.iotesters.{PeekPokeTester, Driver, copyVerilatorHeaderFiles}
import essent._

import java.io.{File, FileWriter}
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.{Files, Paths}

// FUTURE: consider moving this into essent repo/package

object EssentBackend {
  val baseBuildAndTestDirName = "essent_run_dir"

  def buildAndRun[T <: chisel3.Module](dutName: String)(dutGen: () => T)(testerGen: T => PeekPokeTester[T]) = {
    // set up buildDir
    val dir = new File(baseBuildAndTestDirName + "/" + dutName)
    dir.mkdirs()
    val buildDir = dir.getAbsolutePath
    // get firrtl
    val cs = new chisel3.stage.ChiselStage()
    val dutFirrtl = firrtl.Parser.parse(cs.emitFirrtl(dutGen(), Array("--target-dir", buildDir)))
    val firFile = new File(buildDir, s"$dutName.fir")
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
