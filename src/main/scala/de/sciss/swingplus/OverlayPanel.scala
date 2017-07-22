/*
 *  OverlayPanel.scala
 *  (SwingPlus)
 *
 *  Copyright (c) 2013-2017 Hanns Holger Rutz. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package de.sciss.swingplus

import scala.swing.{SequentialContainer, Panel}
import javax.swing.{OverlayLayout, JPanel}

class OverlayPanel extends Panel with SequentialContainer.Wrapper {
  override lazy val peer: JPanel = {
    val res = new JPanel(null) with SuperMixin
    res.setLayout(new OverlayLayout(res))
    res
  }
}