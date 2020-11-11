/*
 *  PaddedIcon.scala
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

import javax.swing.Icon
import scala.swing.Insets
import java.awt.{Graphics, Component}

class PaddedIcon(inner: Icon, insets: Insets) extends Icon {
  def getIconWidth : Int = inner.getIconWidth  + insets.left + insets.right
  def getIconHeight: Int = inner.getIconHeight + insets.top  + insets.bottom

  def paintIcon(c: Component, g: Graphics, x: Int, y: Int): Unit =
    inner.paintIcon(c, g, x + insets.left, y + insets.top)
}