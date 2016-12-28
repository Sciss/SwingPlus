lazy val baseName  = "SwingPlus"
lazy val baseNameL = baseName.toLowerCase

lazy val projectVersion = "0.2.2"
lazy val mimaVersion    = "0.2.0"

name                := baseName
version             := projectVersion
organization        := "de.sciss"
scalaVersion        := "2.11.8"
crossScalaVersions  := Seq("2.12.1", "2.11.8", "2.10.6")
description         := "The missing bits for Scala-Swing (additional components and methods)"
homepage            := Some(url(s"https://github.com/Sciss/${name.value}"))
licenses            := Seq("LGPL v2.1+" -> url("http://www.gnu.org/licenses/lgpl-2.1.txt"))

mimaPreviousArtifacts := Set("de.sciss" %% baseNameL % mimaVersion)

initialCommands in console := 
  """import de.sciss.swingplus._
    |import scala.swing._""".stripMargin

lazy val scalaSwingVersion = "2.0.0-M2"

libraryDependencies in ThisBuild += {
  val sv = scalaVersion.value
  if (sv startsWith "2.10")
    "org.scala-lang" % "scala-swing" % sv
  else if (sv startsWith "2.11")
    "org.scala-lang.modules" %% "scala-swing" % "1.0.2"
  else
    "org.scala-lang.modules" %% "scala-swing" % scalaSwingVersion
}

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-encoding", "utf8", "-Xfuture", "-Xlint")

// ---- publishing ----

publishMavenStyle := true

publishTo :=
  Some(if (isSnapshot.value)
    "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  else
    "Sonatype Releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
  )

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := { val n = name.value
<scm>
  <url>git@github.com:Sciss/{n}.git</url>
  <connection>scm:git:git@github.com:Sciss/{n}.git</connection>
</scm>
<developers>
  <developer>
    <id>sciss</id>
    <name>Hanns Holger Rutz</name>
    <url>http://www.sciss.de</url>
  </developer>
</developers>
}
