name := """cms-backend"""
organization := "com.yuhao"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  guice,
  javaJdbc,
  "org.xerial" % "sqlite-jdbc" % "3.36.0.3",
  "com.google.guava" % "guava" % "23.0",
  "org.projectlombok" % "lombok" % "1.18.20",
  "org.apache.commons" % "commons-lang3" % "3.12.0"
)

/**
 * Docker configs
 */
import com.typesafe.sbt.packager.docker.DockerChmodType
import com.typesafe.sbt.packager.docker.DockerPermissionStrategy
dockerChmodType := DockerChmodType.UserGroupWriteExecute
dockerPermissionStrategy := DockerPermissionStrategy.CopyChown
dockerBaseImage := "openjdk:11-jdk"