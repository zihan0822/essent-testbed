organization := "lbl.gov"

version := "0.1"

name := "playground"

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-deprecation", "-unchecked")


lazy val root = (project in file(".")).dependsOn(chisel, chisel_testers)

lazy val chisel = (project in file("chisel3"))
lazy val chisel_testers = (project in file("chisel-testers"))

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases")
)
