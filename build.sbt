ThisBuild / organization := "org.pchapin"
ThisBuild / scalaVersion := "2.12.6"
ThisBuild / version := "0.0.1"

lazy val root = (project in file("."))
  .settings(
    name := "Imaginary Data Generator",

    // For application.
    libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "2.+",
    libraryDependencies += "org.rogach" %% "scallop" % "3.1.+",

    // For unit tests.
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.+" % "test",
  )
