/*
 *  ScrollBar.scala
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

/** swing.ScrollBar does not fire events. This class rectifies that. */
class ScrollBar extends swing.ScrollBar {
  me =>
  peer.addAdjustmentListener(new java.awt.event.AdjustmentListener {
    def adjustmentValueChanged(e: java.awt.event.AdjustmentEvent) {
      publish(new swing.event.ValueChanged(me))
    }
  })
}