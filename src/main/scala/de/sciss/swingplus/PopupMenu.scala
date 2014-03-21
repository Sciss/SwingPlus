package de.sciss.swingplus

// adapted from scala-swing 2.11, written by Ingo Maier and John Sullivan, and
// originally released under a BSD style license (http://www.scala-lang.org/license.html)

import javax.swing.JPopupMenu
import javax.swing.event.{PopupMenuListener, PopupMenuEvent}
import scala.swing.{Insets, Publisher, SequentialContainer, Component}
import de.sciss.swingplus.event.{PopupMenuCanceled, PopupMenuWillBecomeVisible, PopupMenuWillBecomeInvisible}

/** A popup menu component.
  *
  * Example usage:
  *
  * {{{
  * val popupMenu = new PopupMenu {
  *   contents += new Menu("menu 1") {
  *     contents += new RadioMenuItem("radio 1.1")
  *     contents += new RadioMenuItem("radio 1.2")
  *   }
  *   contents += new Menu("menu 2") {
  *     contents += new RadioMenuItem("radio 2.1")
  *     contents += new RadioMenuItem("radio 2.2")
  *   }
  * }
  * val button = new Button("Show Popup Menu")
  * reactions += {
  *   case e: ButtonClicked => popupMenu.show(button, 0, button.bounds.height)
  * }
  * listenTo(button)
  * }}}
  *
  * The component publishes `PopupMenuCanceled`, `PopupMenuWillBecomeInvisible` and `PopupMenuWillBecomeVisible`
  * events which can be used to determine when the menu is opened or closed.
  *
  * @author  John Sullivan
  * @author  Ingo Maier
  * @author  Hanns Holger Rutz
  * @see javax.swing.JPopupMenu
  */
class PopupMenu extends Component with SequentialContainer.Wrapper with Publisher {
  pop =>

  override lazy val peer: JPopupMenu = new JPopupMenu with SuperMixin

  peer.addPopupMenuListener(new PopupMenuListener {
    def popupMenuCanceled           (e: PopupMenuEvent): Unit = publish(PopupMenuCanceled           (pop))
    def popupMenuWillBecomeInvisible(e: PopupMenuEvent): Unit = publish(PopupMenuWillBecomeInvisible(pop))
    def popupMenuWillBecomeVisible  (e: PopupMenuEvent): Unit = publish(PopupMenuWillBecomeVisible  (pop))
  })

  /** Attaches the menu to a given component and makes it visible at the relative coordinates.
    *
    * @param invoker  the component to which the menu is logically attached
    * @param x        the horizontal coordinate of the top-left corner of the menu, relative to the invoker's position
    * @param y        the vertical coordinate of the top-left corner of the menu, relative to the invoker's position
    */
  def show(invoker: Component, x: Int, y: Int): Unit = peer.show(invoker.peer, x, y)

  def margin: Insets = peer.getMargin

  /** Gets or sets the popup menu's label. Different look and feels may choose
    * to display or not display this.
    */
  def label    : String         = peer.getLabel
  def label_=(s: String): Unit  = peer.setLabel(s)
}