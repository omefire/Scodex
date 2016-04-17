name := """Scodex"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  
scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.webjars" % "bootstrap" % "3.3.4",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "org.xerial" % "sqlite-jdbc" % "3.7.2",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "org.scalaz" %% "scalaz-core" % "7.2.2")

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
