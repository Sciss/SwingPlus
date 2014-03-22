/*
 *  DoClickAction.scala
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

import swing.{Action, AbstractButton}

object DoClickAction {
  def apply(button: AbstractButton): DoClickAction = new DoClickAction(button)
}
/** A simple action that visually triggers a given button. */
class DoClickAction(button: AbstractButton, title0: String = null) extends Action(title0) {
  def apply() { button.doClick() }
}