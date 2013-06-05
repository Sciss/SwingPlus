package de.sciss.swingplus

import scala.swing.{Frame, UIElement}

object Implicits {
  implicit final class SwingPlusFrame(val f: Frame) extends AnyVal {
    def defaultCloseOperation = CloseOperation(f.peer.getDefaultCloseOperation)
    def defaultCloseOperation_=(value: CloseOperation) { f.peer.setDefaultCloseOperation(value.id) }
  }

  implicit final class SwingPlusUIElement(val ui: UIElement) extends AnyVal {
    def width : Int = ui.peer.getWidth
    def height: Int = ui.peer.getHeight
  }
}