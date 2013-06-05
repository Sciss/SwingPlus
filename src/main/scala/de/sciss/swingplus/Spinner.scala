package de.sciss.swingplus

import scala.swing.Component
import javax.swing.SpinnerModel
import javax.swing.event.{ChangeEvent, ChangeListener}
import scala.swing.event.ValueChanged

class Spinner(model0: SpinnerModel) extends Component {
  me =>

  override lazy val peer: javax.swing.JSpinner = new javax.swing.JSpinner(model0) with SuperMixin {}

  // XXX TODO: make value type a type parameter
  def value: Any      = peer.getValue
  def value_=(v: Any) { peer.setValue(v.asInstanceOf[AnyRef])}

  peer.addChangeListener(new ChangeListener {
    def stateChanged(e: ChangeEvent) {
      publish(new ValueChanged(me))
    }
  })
}