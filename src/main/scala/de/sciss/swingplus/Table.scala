/*
 *  Table.scala
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

import javax.swing.{RowSorter, SortOrder}
import javax.swing.table.{AbstractTableModel, DefaultTableModel, TableModel}

class Table extends swing.Table {
  def this(rowData: Array[Array[Any]], columnNames: Seq[_]) = {
    this()
    init(rowData, columnNames)
  }

  def this(rows: Int, columns: Int) = {
    this()
    init(rows, columns)
  }

  def this(model0: TableModel) = {
    this()
    model = model0
  }

  /** Get the current value of the given cell.
    * The given cell coordinates are in view coordinates and thus not
    * necessarily the same as for the model.
    *
    * Fixes bug in scala-swing.
    */
  override def apply(row: Int, column: Int): Any = {
    val mRow = viewToModelRow   (row   )
    val mCol = viewToModelColumn(column)
    model.getValueAt(mRow, mCol)
  }

  def viewToModelRow(idx: Int): Int = peer.convertRowIndexToModel(idx)
  def modelToViewRow(idx: Int): Int = peer.convertRowIndexToView (idx)

  def rowMargin: Int = peer.getRowMargin
  def rowMargin_=(value: Int): Unit = peer.setRowMargin(value)

  /** Programmatically sets the sorted column of the table view. */
  def sort(column: Int, ascending: Boolean = true): Unit = {
    val sorter = peer.getRowSorter
    if (sorter != null) {
      val list = new java.util.ArrayList[RowSorter.SortKey](1)
      list.add(new RowSorter.SortKey(column, if (ascending) SortOrder.ASCENDING else SortOrder.DESCENDING))
      sorter.setSortKeys(list)
    }
  }

  protected def init(rowData: Array[Array[Any]], columnNames: Seq[_]): Unit = {
    model = new AbstractTableModel {
      override def getColumnName(column: Int): String = columnNames(column).toString

      def getRowCount   : Int = rowData     .length
      def getColumnCount: Int = columnNames .length

      def getValueAt(row: Int, col: Int): AnyRef = rowData(row)(col).asInstanceOf[AnyRef]

      override def isCellEditable(row: Int, column: Int) = true

      override def setValueAt(value: Any, row: Int, col: Int): Unit = {
        rowData(row)(col) = value
        fireTableCellUpdated(row, col)
      }
    }
  }

  protected def init(rows: Int, columns: Int): Unit = {
    model = new DefaultTableModel(rows, columns)
  }
}
