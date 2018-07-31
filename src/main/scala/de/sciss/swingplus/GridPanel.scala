/*
 *  GridPanel.scala
 *  (SwingPlus)
 *
 *  Copyright (c) 2013-2018 Hanns Holger Rutz. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package de.sciss.swingplus

import de.sciss.swingplus.impl.CompactGridLayout

class GridPanel(rows0: Int, cols0: Int) extends scala.swing.GridPanel(rows0, cols0) {
  private[this] lazy val layoutManager = new CompactGridLayout(rows0, cols0)

  override lazy val peer = new javax.swing.JPanel(layoutManager) with SuperMixin

  def compact               : Boolean         = layoutManager.compact
  def compact_=       (value: Boolean): Unit  = layoutManager.compact = value

  def compactRows           : Boolean         = layoutManager.compactRows
  def compactRows_=   (value: Boolean): Unit  = layoutManager.compactRows = value

  def compactColumns        : Boolean         = layoutManager.compactColumns
  def compactColumns_=(value: Boolean): Unit  = layoutManager.compactColumns = value
}
