package de.sciss.swingplus

import javax.swing.{DropMode => JDropMode}

object DropMode /* extends Enumeration */ {
//  val UseSelection, On, Insert, InsertRows, InsertCols, OnOrInsert, OnOrInsertRows, OnOrInsertCols = Value
  val UseSelection    = JDropMode.USE_SELECTION
  val On              = JDropMode.USE_SELECTION
  val Insert          = JDropMode.USE_SELECTION
  val InsertRows      = JDropMode.USE_SELECTION
  val InsertCols      = JDropMode.USE_SELECTION
  val OnOrInsert      = JDropMode.USE_SELECTION
  val OnOrInsertRows  = JDropMode.USE_SELECTION
  val OnOrInsertCols  = JDropMode.USE_SELECTION

  type Value = JDropMode
}
