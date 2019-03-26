/*
 *  CompactGridLayout.scala
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

package de.sciss.swingplus.impl

import java.awt.{Container, Dimension, GridLayout}

/** A variant of `GridLayout` that can be horizontally and/or vertically compacted. */
class CompactGridLayout(rows: Int, cols: Int, hgap0: Int, vgap0: Int)
  extends GridLayout(rows, cols, hgap0, vgap0) {

  def this(rows: Int, cols: Int) = this(rows, cols, 0, 0)

  var compactRows     = false
  var compactColumns  = false

  def compact: Boolean = compactRows && compactColumns

  /** Sets both horizontal and vertical compaction flag. */
  def compact_=(value: Boolean): Unit = {
    compactColumns  = value
    compactRows     = value
  }

  override def minimumLayoutSize(parent: Container): Dimension =
    parent.getTreeLock.synchronized {
      perform(parent, mode = 0)
    }

  override def preferredLayoutSize(parent: Container): Dimension =
    parent.getTreeLock.synchronized {
      perform(parent, mode = 1)
    }

  override def layoutContainer(parent: Container): Unit =
    parent.getTreeLock.synchronized {
      perform(parent, mode = 2)
    }

  // 0 - minimum size, 1 - preferred size, 2 - layout
  private final def perform(parent: Container, mode: Int): Dimension =
    parent.getTreeLock.synchronized {
      val insets  = parent.getInsets
      val numComp = parent.getComponentCount

      if (numComp == 0) {
        return if (mode == 2) null else
          new Dimension(insets.left + insets.right, insets.top + insets.bottom)
      }

      var rows    = getRows
      var cols    = getColumns

      if (rows > 0)
        cols = (numComp + rows - 1) / rows
      else
        rows = (numComp + cols - 1) / cols

      val colWidths   = new Array[Int](cols)
      val rowHeights  = new Array[Int](rows)
      val isMin       = mode == 0

      var i = 0
      while (i < numComp) {
        val ri    = i / cols
        val ci    = i % cols
        val comp  = parent.getComponent(i)
        val d     = if (isMin) comp.getMinimumSize else comp.getPreferredSize
        if (colWidths (ci) < d.width ) colWidths (ci) = d.width
        if (rowHeights(ri) < d.height) rowHeights(ri) = d.height

        i += 1
      }

      if (!compactColumns) {
        var max = 0
        i = 0
        while (i < cols) {
          val cw = colWidths(i)
          if (cw > max) max = cw
          i += 1
        }
        java.util.Arrays.fill(colWidths, max)
      }

      if (!compactRows) {
        var max = 0
        i = 0
        while (i < rows) {
          val rh = rowHeights(i)
          if (rh > max) max = rh
          i += 1
        }
        java.util.Arrays.fill(rowHeights, max)
      }

      if (mode == 2) {  // layout
        val pd    = preferredLayoutSize(parent)
        val sx    = (1.0 * parent.getWidth ) / pd.width
        val sy    = (1.0 * parent.getHeight) / pd.height

        var x     = insets.left
        var ci    = 0
        val hGap  = getHgap
        val vGap  = getVgap
        while (ci < cols) {
          var y   = insets.top
          val cw0 = (colWidths(ci) * sx).toInt
          val cw  = if (cw0 > 0) cw0 else 1
          var ri  = 0
          while (ri < rows) {
            val i   = ri * cols + ci
            val rh  = if (ci == 0) {
              val rh0 = (rowHeights(ri) * sy).toInt
              val rh1 = if (rh0 > 0) rh0 else 1
              rowHeights(ri) = rh1
              rh1
            } else {
              rowHeights(ri)
            }
            if (i < numComp) {
              parent.getComponent(i).setBounds(x, y, cw, rh)
            }
            y  += rh + vGap
            ri += 1
          }
          x  += cw + hGap
          ci += 1
        }
        null

      } else {  // calc size

        var wSum = 0
        i = 0
        while (i < cols) {
          wSum += colWidths(i)
          i += 1
        }

        var hSum = 0
        i = 0
        while (i < rows) {
          hSum += rowHeights(i)
          i += 1
        }

        new Dimension(insets.left + insets.right  + wSum + (cols - 1) * getHgap,
                      insets.top  + insets.bottom + hSum + (rows - 1) * getVgap)
      }
    }
}
