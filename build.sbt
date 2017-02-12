lazy val quilltests = project.in(file(".")).enablePlugins(AutomateHeaderPlugin, GitVersioning)

libraryDependencies ++= Vector(
  Library.quill,
  Library.akkaActor,
  Library.akkaStream,
  Library.logback,
  Library.scalaTest % "test"
)

initialCommands := """|import de.beikern.quilltests._
                      |""".stripMargin
