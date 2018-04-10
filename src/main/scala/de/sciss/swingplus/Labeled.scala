/*
 *  Labeled.scala
 *  (SwingPlus)
 *
 *  Copyright (c) 2013-2018 Hanns Holger Rutz. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package de.sciss.swingplus

final case class Labeled[A](value: A)(label: String) {
  override def toString: String = label
}