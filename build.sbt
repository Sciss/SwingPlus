lazy val baseName  = "SwingPlus"
lazy val baseNameL = baseName.toLowerCase

lazy val projectVersion = "0.5.0"
lazy val mimaVersion    = "0.5.0"

lazy val scalaSwingVersion = "3.0.0"

// sonatype plugin requires that these are in global
ThisBuild / version      := projectVersion
ThisBuild / organization := "de.sciss"

lazy val root = project.withId(baseNameL).in(file("."))
  .settings(
    name                := baseName,
//    version             := projectVersion,
//    organization        := "de.sciss",
    scalaVersion        := "2.13.4",
    crossScalaVersions  := Seq("3.0.0-M3", "2.13.4", "2.12.12"),
    description         := "The missing bits for Scala-Swing (additional components and methods)",
    homepage            := Some(url(s"https://git.iem.at/sciss/${name.value}")),
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
        case Some((3, _))            => sourceDir / "scala-2.13+"
        case _                       => sourceDir / "scala-2.13-"
      }
    }
  )
  .settings(publishSettings)


// ---- publishing ----
lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  developers := List(
    Developer(
      id    = "sciss",
      name  = "Hanns Holger Rutz",
      email = "contact@sciss.de",
      url   = url("https://www.sciss.de")
    )
  ),
  scmInfo := {
    val h = "git.iem.at"
    val a = s"sciss/${name.value}"
    Some(ScmInfo(url(s"https://$h/$a"), s"scm:git@$h:$a.git"))
  },
)

