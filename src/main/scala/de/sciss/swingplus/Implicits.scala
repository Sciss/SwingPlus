package de.sciss.swingplus

import scala.swing.{Component, Frame, UIElement}
import scala.swing.Action
import java.awt.{event => jawte}
import javax.{swing => js}

object Implicits {
  implicit final class SwingPlusFrame(val f: Frame) extends AnyVal {
    def defaultCloseOperation        : CloseOperation         = CloseOperation(f.peer.getDefaultCloseOperation)
    def defaultCloseOperation_=(value: CloseOperation): Unit  = f.peer.setDefaultCloseOperation(value.id)
  }

  //  private final class ClientProps(peer: js.JComponent) extends mutable.Map[String, Any] {
  //    def get(key: String): Option[Any] = Option(peer.getClientProperty(key))
  //
  //    def +=(kv: (String, Any)): this.type = {
  //      peer.putClientProperty(kv._1, kv._2)
  //      this
  //    }
  //
  //    def -=(key: String): this.type = {
  //      peer.putClientProperty(key, null)
  //      this
  //    }
  //
  //    def iterator: Iterator[(String, Any)] = {
  //      val f = peer.getClass.getDeclaredField("clientProperties")
  //      f.setAccessible(true)
  //      val table = f.get(peer).asInstanceOf[js.ArrayTable]
  //    }
  //  }

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

    //    def putClientProperty   (key: String, value: Any): Unit   = component.peer.putClientProperty(key, value)
    //    def removeClientProperty(key: String)            : Unit   = component.peer.putClientProperty(key, null )
    //    def getClientProperty   (key: String): Option[Any] = Option(component.peer.getClientProperty(key))
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