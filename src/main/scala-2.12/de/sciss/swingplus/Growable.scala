package de.sciss.swingplus

abstract class Growable[-A] extends scala.collection.generic.Growable[A] {
  final def += (elem: A): this.type = addOne(elem)

  def addOne(elem: A): this.type
}