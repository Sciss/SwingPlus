package de.sciss.swingplus

final case class Labeled[A](value: A)(label: String) {
  override def toString = label
}