lazy val quilltests = project
  .in(file("."))
  .enablePlugins(AutomateHeaderPlugin, GitVersioning)

libraryDependencies ++= Vector(
  Library.scalaTest % "test"
)

initialCommands := """|import de.beikern.quilltests._
                      |""".stripMargin
