import sbt._

object Version {
  final val Scala     = "2.11.8"
  final val ScalaTest = "3.0.0"
  final val Quill = "0.8.0"
  final val Akka = "2.4.9-RC2"
  final val Logback = "1.0.13"
}

object Library {
  val quill       = "io.getquill" %% "quill-cassandra" % Version.Quill
  val akkaActor   = "com.typesafe.akka" %% "akka-actor" % Version.Akka
  val akkaStream  = "com.typesafe.akka" %% "akka-stream" % Version.Akka
  val logback     = "ch.qos.logback" % "logback-classic" % Version.Logback
  val scalaTest   = "org.scalatest" %% "scalatest" % Version.ScalaTest
}
