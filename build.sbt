organization := "ucsc.edu"

version := "0.8"

name := "essent.testbed"

mainClass in (Compile, run) := Some("essent.testbed.Launcher")

scalaVersion := "2.12.13"

def scalacOptionsVersion(scalaVersion: String): Seq[String] = {
  Seq() ++ {
    // If we're building with Scala > 2.11, enable the compile option
    //  switch to support our anonymous Bundle definitions:
    //  https://github.com/scala/bug/issues/10047
    CrossVersion.partialVersion(scalaVersion) match {
      case Some((2, scalaMajor: Long)) if scalaMajor < 12 => Seq()
      case _ => Seq("-Xsource:2.11")
    }
  }
}

scalacOptions ++= scalacOptionsVersion(scalaVersion.value)

scalacOptions ++= Seq("-deprecation", "-feature", "-language:reflectiveCalls")

libraryDependencies += "edu.berkeley.cs" %% "firrtl" % "1.4.3"

libraryDependencies += "edu.berkeley.cs" %% "chisel3" % "3.4.1"

libraryDependencies += "edu.berkeley.cs" %% "firrtl-interpreter" % "1.4.3"

libraryDependencies += "edu.berkeley.cs" %% "chisel-iotesters" % "1.4.3"

lazy val essent = (project in file("essent"))

lazy val root = (project in file(".")).dependsOn(essent)
