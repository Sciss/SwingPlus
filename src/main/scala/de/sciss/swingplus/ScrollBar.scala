/*
 *  ScrollBar.scala
 *  (SwingPlus)
 *
 *  Copyright (c) 2013-2019 Hanns Holger Rutz. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package de.sciss.swingplus

import javax.swing.JScrollBar

import scala.swing.Orientation

/** swing.ScrollBar does not fire events. This class rectifies that. */
class ScrollBar(orientation0: Orientation.Value, value0: Int, blockIncrement0: Int, minimum0: Int, maximum0: Int)
  extends swing.ScrollBar {

  me =>

  def this(orientation0: Orientation.Value) =
    this(orientation0, value0 = 0, blockIncrement0 = 10, minimum0 = 0, maximum0 = 100)

  def this() = this(Orientation.Vertical)

  override lazy val peer: JScrollBar =
    new JScrollBar(orientation0.id, value0, blockIncrement0, minimum0, maximum0) with SuperMixin

  peer.addAdjustmentListener(new java.awt.event.AdjustmentListener {
    def adjustmentValueChanged(e: java.awt.event.AdjustmentEvent): Unit =
      publish(new swing.event.ValueChanged(me))
  })
}