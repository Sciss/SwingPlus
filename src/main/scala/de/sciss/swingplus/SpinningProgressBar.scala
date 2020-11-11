/*
 *  SpinningProgressBar.scala
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

import java.awt.EventQueue

import scala.swing.{Swing, ProgressBar}
import Swing._

class SpinningProgressBar extends OverlayPanel {
  @volatile private var _spin = false

  /** This method is thread safe. */
  def spinning: Boolean = _spin
  def spinning_=(value: Boolean): Unit = {
    _spin = value
    if (EventQueue.isDispatchThread) setVisibility(_spin)
    else Swing.onEDT(setVisibility(_spin))
  }

  private def setVisibility(b: Boolean): Unit = ggBusy.visible = _spin

  private val ggBusy: ProgressBar = new ProgressBar {
    visible       = false
    indeterminate = true
    preferredSize = (24, 24)
    peer.putClientProperty("JProgressBar.style", "circular")
  }

  contents += RigidBox((24, 24))
  contents += ggBusy
}