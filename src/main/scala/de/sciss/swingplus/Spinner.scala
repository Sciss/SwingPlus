/*
 *  Spinner.scala
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

package de.sciss.swingplus

import scala.swing.Component
import javax.swing.SpinnerModel
import javax.swing.event.{ChangeEvent, ChangeListener}
import scala.swing.event.ValueChanged

class Spinner(model0: SpinnerModel) extends Component {
  me =>

  override lazy val peer: javax.swing.JSpinner = new javax.swing.JSpinner(model0) with SuperMixin {
    // bug with aqua look and feel. JSpinner relies on getComponent(0),
    // which might not have a baseline. Fall back to editor's baseline then.
    override def getBaseline(width: Int, height: Int): Int = {
      val res = super.getBaseline(width, height)
      if (res >= 0) res else {
        getEditor.getBaseline(width, height)
      }
    }
  }

  // XXX TODO: make value type a type parameter
  def value    : Any        = peer.getValue
  def value_=(v: Any): Unit = peer.setValue(v.asInstanceOf[AnyRef])

  peer.addChangeListener(new ChangeListener {
    def stateChanged(e: ChangeEvent): Unit = publish(new ValueChanged(me))
  })
}