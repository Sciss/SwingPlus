package de.sciss.swingplus
package event

import scala.swing.event.ComponentEvent

// adapted from scala-swing 2.11, written by Ingo Maier and John Sullivan

abstract sealed class PopupMenuEvent extends ComponentEvent {
  override val source: PopupMenu
}

case class PopupMenuCanceled            (source: PopupMenu) extends PopupMenuEvent
case class PopupMenuWillBecomeInvisible (source: PopupMenu) extends PopupMenuEvent
case class PopupMenuWillBecomeVisible   (source: PopupMenu) extends PopupMenuEvent