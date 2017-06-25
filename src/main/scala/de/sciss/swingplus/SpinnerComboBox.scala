/*
 *  SpinnerComboBox.scala
 *  (SwingPlus)
 *
 *  Copyright (c) 2013-2014 Hanns Holger Rutz. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package de.sciss.swingplus

import javax.swing.{SpinnerNumberModel, AbstractSpinnerModel}

import scala.swing.Swing

/** A `ComboBox` for editable numbers. Numbers such as `Int` or `Double` are presented
  * inside a `Spinner` editor component. The combo-box carries a list of preset numbers.
  */
class SpinnerComboBox[A](value0: A, minimum: A, maximum: A, step: A, items: Seq[A])(implicit num: Numeric[A])
  extends ComboBox[A](items) {

  private[this] val sm: AbstractSpinnerModel =
    (value0.asInstanceOf[AnyRef], minimum.asInstanceOf[AnyRef], maximum.asInstanceOf[AnyRef], step.asInstanceOf[AnyRef]) match {
      case (n: Number, min: Comparable[_], max: Comparable[_], s: Number) =>
        new SpinnerNumberModel(n, min, max, s)
      case _ =>
        new AbstractSpinnerModel {
          private[this] var _value = value0

          def getValue: AnyRef = _value.asInstanceOf[AnyRef]
          def setValue(value: Any): Unit = _value = value.asInstanceOf[A]

          def getNextValue    : AnyRef = clip(num.plus (_value, step)).asInstanceOf[AnyRef]
          def getPreviousValue: AnyRef = clip(num.minus(_value, step)).asInstanceOf[AnyRef]
        }
    }

  private def clip(in: A): A = num.max(minimum, num.min(maximum, in))

  private[this] val sp = new Spinner(sm)

  def spinner: Spinner = sp

  private object editor extends ComboBox.Editor[A] {
    def component: swing.Component = sp

    def item: A = sp.value.asInstanceOf[A]
    def item_=(a: A): Unit = if (a != null) sp.value = a  // CCC

    def startEditing(): Unit = comboBoxPeer.selectAll()
  }

  def value: A = sp.value.asInstanceOf[A]

  def value_=(value: A): Unit = sp.value = clip(value)

  // ---- init ----

  makeEditable()(_ => editor)
  border = Swing.EmptyBorder(0, 0, 0, 4)
}
