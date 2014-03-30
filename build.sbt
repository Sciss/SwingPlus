name                := "SwingPlus"

version             := "0.1.1"

organization        := "de.sciss"

scalaVersion        := "2.10.4"

crossScalaVersions  := Seq("2.11.0-RC3", "2.10.4")

description         := "The missing bits for Scala-Swing (additional components and methods)"

homepage            := Some(url("https://github.com/Sciss/" + name.value))

licenses            := Seq("LGPL v3+" -> url("http://www.gnu.org/licenses/lgpl-3.0.txt"))

initialCommands in console := 
  """import de.sciss.swingplus._
    |import scala.swing._""".stripMargin

libraryDependencies in ThisBuild += {
  val sv = scalaVersion.value
  if (sv startsWith "2.10")
    "org.scala-lang" % "scala-swing" % sv
  else
    "org.scala-lang.modules" %% "scala-swing" % "1.0.1"
}

retrieveManaged := true

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

// ---- publishing ----

publishMavenStyle := true

publishTo :=
  Some(if (version.value endsWith "-SNAPSHOT")
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

// ---- ls.implicit.ly ----

seq(lsSettings :_*)

(LsKeys.tags   in LsKeys.lsync) := Seq("swing", "gui")

(LsKeys.ghUser in LsKeys.lsync) := Some("Sciss")

(LsKeys.ghRepo in LsKeys.lsync) := Some(name.value)

