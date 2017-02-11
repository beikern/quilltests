import sbt._

object Version {
  final val Scala     = "2.11.8"
  final val ScalaTest = "3.0.0"
  final val Quill = "1.1.0"
  final val Akka = "2.4.17"
  final val Logback = "1.0.13"
}

object Library {
  val quill: ModuleID = "io.getquill" %% "quill-cassandra" % Version.Quill
  val akkaActor: ModuleID = "com.typesafe.akka" %% "akka-actor" % Version.Akka
  val akkaStream: ModuleID = "com.typesafe.akka" %% "akka-stream" % Version.Akka
  val logback: ModuleID = "ch.qos.logback" % "logback-classic" % Version.Logback
  val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % Version.ScalaTest
}
