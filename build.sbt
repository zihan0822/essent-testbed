organization := "lbl.gov"

version := "0.1"

name := "playground"

scalaVersion := "2.11.12"

scalacOptions ++= Seq("-deprecation", "-unchecked")

lazy val firrtl = (project in file("firrtl"))

lazy val chisel_testers = (project in file("chisel-testers")).dependsOn(firrtl)

lazy val essent = (project in file("essent")).dependsOn(firrtl)

lazy val root = (project in file(".")).dependsOn(chisel_testers, essent)

libraryDependencies += "com.github.scopt" %% "scopt" % "3.6.0"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

libraryDependencies += "edu.berkeley.cs" %% "chisel3" % "3.1.0"

libraryDependencies += "edu.berkeley.cs" %% "firrtl-interpreter" % "1.1.0"

// ANTLRv4

enablePlugins(Antlr4Plugin)

antlr4GenVisitor in Antlr4 := true // default = false

antlr4GenListener in Antlr4 := false // default = true

antlr4PackageName in Antlr4 := Option("firrtl.antlr")

antlr4Version in Antlr4 := "4.7"
