/*
 *  DropMode.scala
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

import javax.swing.{DropMode => JDropMode}

object DropMode {
  val UseSelection    = JDropMode.USE_SELECTION
  val On              = JDropMode.ON
  val Insert          = JDropMode.INSERT
  val InsertRows      = JDropMode.INSERT_ROWS
  val InsertCols      = JDropMode.INSERT_COLS
  val OnOrInsert      = JDropMode.ON_OR_INSERT
  val OnOrInsertRows  = JDropMode.ON_OR_INSERT_ROWS
  val OnOrInsertCols  = JDropMode.ON_OR_INSERT_COLS

  type Value = JDropMode
}
