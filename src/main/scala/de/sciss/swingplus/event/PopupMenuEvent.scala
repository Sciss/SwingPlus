/*
 *  PopupMenuEvent.scala
 *  (SwingPlus)
 *
 *  Copyright (c) 2013-2014 Hanns Holger Rutz. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v3+
 *
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package de.sciss.swingplus
package event

import scala.swing.event.ComponentEvent

// adapted from scala-swing 2.11, written by Ingo Maier and John Sullivan

/**
 * @author  Ingo Maier
 * @author  John Sullivan
 */
abstract sealed class PopupMenuEvent extends ComponentEvent {
  override val source: PopupMenu
}

case class PopupMenuCanceled            (source: PopupMenu) extends PopupMenuEvent
case class PopupMenuWillBecomeInvisible (source: PopupMenu) extends PopupMenuEvent
case class PopupMenuWillBecomeVisible   (source: PopupMenu) extends PopupMenuEvent
