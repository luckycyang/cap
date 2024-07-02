import mill._, scalalib._

object cap extends RootModule with ScalaModule {
  def scalaVersion = "2.13.14"
  def ivyDeps = Agg(
    ivy"org.chipsalliance::chisel:6.4.0",
	ivy"edu.berkeley.cs::chiseltest:6.0.0"
  )

  def scalacPluginIvyDeps = Agg(ivy"org.chipsalliance:::chisel-plugin:6.4.0")
  // def mainClass = Some("Main")
  object test extends ScalaTests with TestModule.ScalaTest {
    def ivyDeps = Agg(
      ivy"org.scalatest::scalatest:3.2.9"
    )
  }
}
