lazy val baseName  = "SwingPlus"
lazy val baseNameL = baseName.toLowerCase

lazy val projectVersion = "0.4.2"
lazy val mimaVersion    = "0.4.0"

lazy val scalaSwingVersion = "2.1.1"

lazy val root = project.withId(baseNameL).in(file("."))
  .settings(
    name                := baseName,
    version             := projectVersion,
    organization        := "de.sciss",
    scalaVersion        := "2.12.8",
    crossScalaVersions  := Seq("2.12.8", "2.11.12", "2.13.0-RC1"),
    description         := "The missing bits for Scala-Swing (additional components and methods)",
    homepage            := Some(url(s"https://github.com/Sciss/${name.value}")),
    licenses            := Seq("LGPL v2.1+" -> url("http://www.gnu.org/licenses/lgpl-2.1.txt")),
    scalacOptions      ++= Seq("-deprecation", "-unchecked", "-feature", "-encoding", "utf8", "-Xlint", "-Xsource:2.13"),
    mimaPreviousArtifacts := Set("de.sciss" %% baseNameL % mimaVersion),
    initialCommands in console := 
      """import de.sciss.swingplus._
        |import scala.swing._""".stripMargin,
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-swing" % scalaSwingVersion
    ),
    unmanagedSourceDirectories in Compile += {
      val sourceDir = (sourceDirectory in Compile).value
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n >= 13 => sourceDir / "scala-2.13+"
        case _                       => sourceDir / "scala-2.13-"
      }
    }
  )
  .settings(publishSettings)


// ---- publishing ----
lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo := {
    Some(if (isSnapshot.value)
      "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
    else
      "Sonatype Releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
    )
  },
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
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
)
