/*
 *  Implicits.scala
 *  (SwingPlus)
 *
 *  Copyright (c) 2013-2020 Hanns Holger Rutz. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package de.sciss.swingplus

import java.awt.{event => jawte}

import javax.{swing => js}

import scala.swing.{AbstractButton, Action, ButtonGroup, Component, Frame, UIElement}

/** Contains various extension methods for existing Swing components. */
object Implicits {
  implicit final class SwingPlusFrame(val `this`: Frame) extends AnyVal { me =>
    import me.{`this` => f}
    def defaultCloseOperation        : CloseOperation         = CloseOperation(f.peer.getDefaultCloseOperation)
    def defaultCloseOperation_=(value: CloseOperation): Unit  = f.peer.setDefaultCloseOperation(value.id)
  }

  implicit final class SwingPlusUIElement(val `this`: UIElement) extends AnyVal { me =>
    import me.{`this` => ui}
    def width : Int = ui.peer.getWidth
    def height: Int = ui.peer.getHeight
  }

  implicit final class SwingPlusComponent(val `this`: Component) extends AnyVal { me =>
    import me.{`this` => component}
    def baseline: Int = {
      val p = component.peer
      baseline(p.getWidth, p.getHeight)
    }
    def baseline(width: Int, height:Int): Int = component.peer.getBaseline(width, height)

    def clientProps: ClientProperties = new ClientProperties(component)
  }

  private final class ActionWrap(peer0: js.Action) extends Action(null) {
    override lazy val peer: js.Action = peer0

    // should typically not be invoked, but who knows...
    def apply(): Unit = peer.actionPerformed(new jawte.ActionEvent(this, jawte.ActionEvent.ACTION_PERFORMED, ""))
  }

  implicit final class SwingPlusActionType(val `this`: Action.type) extends AnyVal {
    def wrap(peer: javax.swing.Action): Action = new ActionWrap(peer)
  }

  implicit final class SwingPlusButtonGroup(val `this`: ButtonGroup) extends AnyVal { me =>
    import me.{`this` => bg}

    def clearSelection(): Unit = bg.peer.clearSelection()

    def selected_=(value: Option[AbstractButton]): Unit = value match {
      case Some(b)  => bg.select(b)
      case None     => clearSelection()
    }
  }
}