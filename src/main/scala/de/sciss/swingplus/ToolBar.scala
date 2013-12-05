package de.sciss.swingplus

import javax.swing.JToolBar
import scala.swing.{Insets, Dimension, SequentialContainer, Component, Orientation}

class ToolBar(orientation: Orientation.Value)(contents0: Component*)
  extends Component with SequentialContainer.Wrapper {

  def this(contents0: Component*) = this(Orientation.Horizontal)(contents0: _*)
  def this()                      = this(Orientation.Horizontal)()

  override lazy val peer: JToolBar = new JToolBar(orientation.id) with SuperMixin

  contents ++= contents0

  def addSeparator()                 : Unit = peer.addSeparator()
  def addSeparator(size: Dimension)  : Unit = peer.addSeparator(size)

  def margin               : Insets         = peer.getMargin
  def margin_=       (value: Insets ): Unit = peer.setMargin(value)

  def borderPainted        : Boolean        = peer.isBorderPainted
  def borderPainted_=(value: Boolean): Unit = peer.setBorderPainted(value)

  def floatable            : Boolean        = peer.isFloatable
  def floatable_=    (value: Boolean): Unit = peer.setFloatable(value)

  def rollover             : Boolean        = peer.isRollover
  def rollover_=     (value: Boolean): Unit = peer.setRollover(value)
}
