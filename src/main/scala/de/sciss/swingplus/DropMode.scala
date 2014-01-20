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
