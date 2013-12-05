package de.sciss.swingplus

import javax.swing.JSeparator
import scala.swing.{Orientation, Component}

object Separator {
  def apply(orientation: Orientation.Value = Orientation.Horizontal): Separator = new Separator(orientation)
}
class Separator(orientation: Orientation.Value) extends Component {
  override lazy val peer: JSeparator = new JSeparator with SuperMixin
}
