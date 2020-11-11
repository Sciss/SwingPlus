/*
 *  EditorPane.scala
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

import javax.swing.text.{Document, Highlighter}

import scala.swing.Color

trait TextComponent extends swing.TextComponent {

  def document: Document = peer.getDocument
  def document_=(value: Document): Unit = peer.setDocument(value)

  def caretColor: Color = peer.getCaretColor
  def caretColor_=(value: Color): Unit = peer.setCaretColor(value)

  def selectionColor: Color = peer.getSelectionColor
  def selectionColor_=(value: Color): Unit = peer.setSelectionColor(value)

  def selectedTextColor: Color = peer.getSelectedTextColor
  def selectedTextColor_=(value: Color): Unit = peer.setSelectedTextColor(value)

  def disabledTextColor: Color = peer.getDisabledTextColor
  def disabledTextColor_=(value: Color): Unit = peer.setDisabledTextColor(value)
  
  def highlighter: Highlighter = peer.getHighlighter
  def highlighter_=(value: Highlighter): Unit = peer.setHighlighter(value)
  
  def selectionStart: Int = peer.getSelectionStart
  def selectionStart_=(value: Int): Unit = peer.setSelectionStart(value)

  def selectionEnd: Int = peer.getSelectionEnd
  def selectionEnd_=(value: Int): Unit = peer.setSelectionEnd(value)
}

class EditorPane(contentType0: String, text0: String) extends swing.EditorPane(contentType0, text0) with TextComponent