/*
 *  Separator.scala
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

import javax.swing.JSeparator
import scala.swing.{Orientation, Component}

object Separator {
  def apply(orientation: Orientation.Value = Orientation.Horizontal): Separator = new Separator(orientation)
}
class Separator(orientation: Orientation.Value) extends Component {
  override lazy val peer: JSeparator = new JSeparator with SuperMixin
}
