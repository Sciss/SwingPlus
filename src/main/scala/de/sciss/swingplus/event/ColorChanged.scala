package de.sciss.swingplus.event

import scala.swing.event.{SelectionEvent, ComponentEvent}
import scala.swing.{Color, Component}

/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2007-2013, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

//package scala.swing
//package event

case class ColorChanged(source: Component, c: Color) extends ComponentEvent with SelectionEvent
