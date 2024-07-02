ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"
val chiselVersion = "6.4.0"
lazy val root = (project in file("."))
  .settings(
    name := "cap",
    addCompilerPlugin("org.chipsalliance" % "chisel-plugin" % chiselVersion cross CrossVersion.full),
    libraryDependencies += "org.chipsalliance" %% "chisel" % chiselVersion,
    libraryDependencies += "edu.berkeley.cs" %% "chiseltest" % "6.0.0",
  )


