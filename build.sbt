enablePlugins(JavaAppPackaging)

name := "polytags"

version := "0.1"

scalaVersion := "2.12.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV       = "2.4.19"
  val akkaHttpV   = "10.0.9"
  val scalaTestV  = "3.0.1"
  val reactiveV   = "0.12.7"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV,
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV,
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "ch.qos.logback"    % "logback-classic" % "1.2.3",
    "org.reactivemongo" %% "reactivemongo" % reactiveV,
    "nu.validator.htmlparser" % "htmlparser" % "1.4",
    "org.scala-lang.modules" %% "scala-xml" % "1.1.0",
    "com.nulab-inc" %% "scala-oauth2-core" % "1.3.0",
    "org.scalatest"     %% "scalatest" % scalaTestV % "test"
  )
}

parallelExecution in Test := false

Revolver.settings