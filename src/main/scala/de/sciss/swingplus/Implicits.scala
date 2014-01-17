package de.sciss.swingplus

import scala.swing.{ListView, Component, Frame, UIElement, Action}
import java.awt.{event => jawte}
import javax.{swing => js}

object Implicits {
  implicit final class SwingPlusFrame(val f: Frame) extends AnyVal {
    def defaultCloseOperation        : CloseOperation         = CloseOperation(f.peer.getDefaultCloseOperation)
    def defaultCloseOperation_=(value: CloseOperation): Unit  = f.peer.setDefaultCloseOperation(value.id)
  }

  implicit final class SwingPlusUIElement(val ui: UIElement) extends AnyVal {
    def width : Int = ui.peer.getWidth
    def height: Int = ui.peer.getHeight
  }

  implicit final class SwingPlusComponent(val component: Component) extends AnyVal {
    def baseline: Int = {
      val p = component.peer
      baseline(p.getWidth, p.getHeight)
    }
    def baseline(width: Int, height:Int): Int = component.peer.getBaseline(width, height)

    def clientProps: ClientProperties = new ClientProperties(component)
  }

  implicit final class SwingPlusListView[A](val component: ListView[A]) extends AnyVal {
    import component.peer
    def dragEnabled        : Boolean               = peer.getDragEnabled
    def dragEnabled_=(value: Boolean): Unit        = peer.setDragEnabled(value)
    def dropMode           : DropMode.Value        = peer.getDropMode // DropMode(peer.getDropMode.ordinal())
    def dropMode_=   (value: DropMode.Value): Unit = peer.setDropMode(value) // (javax.swing.DropMode.values()(value.id))
  }

  private final class ActionWrap(peer0: js.Action) extends Action(null) {
    override lazy val peer: js.Action = peer0

    // should typically not be invoked, but who knows...
    def apply(): Unit = peer.actionPerformed(new jawte.ActionEvent(this, jawte.ActionEvent.ACTION_PERFORMED, ""))
  }

  implicit final class SwingPlusActionType(val self: Action.type) extends AnyVal {
    def wrap(peer: javax.swing.Action): Action = new ActionWrap(peer)
  }
}