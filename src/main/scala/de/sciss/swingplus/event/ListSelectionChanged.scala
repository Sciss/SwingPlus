package de.sciss.swingplus
package event

import scala.swing.event.SelectionChanged

object ListSelectionChanged {
  def unapply[A](e: ListSelectionChanged[A]): Option[(ListView[A], Range, Boolean)] =
    Some((e.source, e.range, e.live))
}

class ListSelectionChanged[A](override val source: ListView[A], val range: Range, val live: Boolean)
  extends SelectionChanged(source) with ListEvent[A]
