name := """BitTracking"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

lazy val myProject = (project in file("."))
  .enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "mysql" % "mysql-connector-java" % "5.1.36",
  "org.webjars" % "bootstrap" % "3.3.5",
"org.apache.directory.studio" % "org.apache.commons.io" % "2.4",
  "com.google.code.gson" % "gson" % "2.3.1",
"org.easytesting" % "fest-assert" % "1.4" % Test,
  "com.typesafe.play" %% "play-mailer" % "2.4.1"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
