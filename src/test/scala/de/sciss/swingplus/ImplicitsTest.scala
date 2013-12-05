package de.sciss.swingplus

import scala.swing.{Button, MainFrame, Frame, SimpleSwingApplication}
import Implicits._

object ImplicitsTest extends SimpleSwingApplication {
  lazy val top: Frame = new MainFrame {
    title = "SwingPlus"

    val b = Button("Foo") { println("bar") }
    b.clientProps += "JButton.buttonType" -> "textured" // .text = "Baz"

    contents = b
    pack()
    centerOnScreen()
  }
}
