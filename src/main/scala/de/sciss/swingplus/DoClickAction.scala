package de.sciss.swingplus

import swing.{Action, AbstractButton}

object DoClickAction {
  def apply(button: AbstractButton): DoClickAction = new DoClickAction(button)
}
/** A simple action that visually triggers a given button. */
class DoClickAction(button: AbstractButton, title0: String = null) extends Action(title0) {
  def apply() { button.doClick() }
}