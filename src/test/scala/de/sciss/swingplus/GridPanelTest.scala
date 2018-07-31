package de.sciss.swingplus

import scala.swing.{Frame, Label, MainFrame, SimpleSwingApplication, Slider}

object GridPanelTest extends SimpleSwingApplication {
  lazy val top: Frame =
    new MainFrame {
      title = "GridPanel"

      contents = new GridPanel(2, 2) {
        compactColumns = true
        contents ++= Seq(
          new Label("Foo:"), new Slider,
          new Label("Bar:"), new Slider { paintTicks = true }
        )
      }
    }
}
