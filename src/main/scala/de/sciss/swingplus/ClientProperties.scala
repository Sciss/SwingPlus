/*
 *  ClientProperties.scala
 *  (SwingPlus)
 *
 *  Copyright (c) 2013-2020 Hanns Holger Rutz. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package de.sciss.swingplus

import scala.swing.Component

/** Swing component client properties are hidden accept for put and get. Therefore it lacks
  * any sort of iterator and we cannot implement `collection.mutable.Map`. This is a
  * compromise which provides a few methods known from `collection.mutable.Map`
  */
final class ClientProperties(val component: Component) extends AnyVal {
  def +=(kv: (String, Any)): Component = {
    update(kv._1, kv._2)
    component
  }

  def -=(key: String): Component = {
    update(key, null)
    component
  }

  def get(key: String): Option[Any] = Option(component.peer.getClientProperty(key))

  def put(key: String, value: Any): Option[Any] = {
    val res = get(key)
    update(key, value)
    res
  }

  def remove(key: String): Option[Any] = {
    val res = get(key)
    this -= key
    res
  }

  def update(key: String, value: Any): Unit = component.peer.putClientProperty(key, value)
}
