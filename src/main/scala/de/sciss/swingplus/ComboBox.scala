// This is a version of the original Scala-Swing class, adapted to
// compile under Java 7, and capable of being used in projects that
// compile with either Java 6 and 7. In other words, the idiotic
// generification of Java-Swing under Java 7 is hidden. The code
// has further been cleaned up.

package de.sciss.swingplus

/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2007-2013, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

import javax.swing.{ListCellRenderer, JComponent, JComboBox, JTextField, ComboBoxModel, AbstractListModel}
import java.awt.event.ActionListener

import scala.swing.event.{SelectionChanged, ActionEvent}
import scala.swing.{Swing, Component, Reactions, Publisher}
import scala.util.control.NonFatal

import scala.language.implicitConversions

object ComboBox {
  /** An editor for a combo box. Let's you edit the currently selected item.
    * It is highly recommended to use the BuiltInEditor class. For anything
    * else, one cannot guarantee that it integrates nicely with the current
    * LookAndFeel.
    *
    * Publishes action events.
    */
  trait Editor[A] extends Publisher {
    lazy val comboBoxPeer: javax.swing.ComboBoxEditor = new javax.swing.ComboBoxEditor with Publisher {
      def addActionListener(l: ActionListener): Unit =
        this match {
          // TODO case w: Action.Trigger.Wrapper =>
          //  w.peer.addActionListener(l)
          case _ =>
            val listener = new Reactions.Wrapper(l) ({
              case ActionEvent(c) => l.actionPerformed(new java.awt.event.ActionEvent(c.peer, 0, ""))
            })
            // not accessible outside of `swing` package:
            // this.subscribe(listener)
            listeners += listener
        }

      def removeActionListener(l: ActionListener): Unit =
        this match {
          // TODO case w: Action.Trigger.Wrapper =>
          //  w.peer.removeActionListener(l)
          case _ =>
            val listener = new Reactions.Wrapper(l)({ case _ => })
            // not accessible outside of `swing` package:
            // this.unsubscribe(listener)
            listeners -= listener
        }

      def getEditorComponent: JComponent = Editor.this.component.peer
      def getItem: AnyRef = item.asInstanceOf[AnyRef]
      def selectAll(): Unit = startEditing()
      def setItem(a: Any): Unit =
        item = a.asInstanceOf[A]
    }
    def component: Component
    def item: A
    def item_=(a: A): Unit
    def startEditing(): Unit
  }

  /** Use this editor, if you want to reuse the builtin editor supplied by the current
    * Look and Feel. This is restricted to a text field as the editor widget. The
    * conversion from and to a string is done by the supplied functions.
    *
    * It's okay if string2A throws exceptions. They are caught by an input verifier.
    */
  class BuiltInEditor[A](comboBox: ComboBox[A])(string2A: String => A,
                                                a2String: A => String) extends ComboBox.Editor[A] {
    protected /* [swing] */ class DelegatedEditor(editor: javax.swing.ComboBoxEditor)
      extends javax.swing.ComboBoxEditor {

      var value: A = {
        val v = comboBox.selection.item // .peer.getSelectedItem
        try {
          v match {
            case s: String => string2A(s)
            case _ => v.asInstanceOf[A]
          }
        } catch {
          case NonFatal(_) =>
            throw new IllegalArgumentException(s"ComboBox not initialized with a proper value, was '$v'.")
        }
      }
      def addActionListener   (l: ActionListener): Unit = editor.addActionListener   (l)
      def removeActionListener(l: ActionListener): Unit = editor.removeActionListener(l)

      def getEditorComponent: JComponent = editor.getEditorComponent.asInstanceOf[JComponent]

      def selectAll(): Unit = editor.selectAll()

      def getItem: AnyRef = { verifier.verify(getEditorComponent); value.asInstanceOf[AnyRef] }
      def setItem(a: Any): Unit = editor.setItem(a)

      val verifier: javax.swing.InputVerifier = new javax.swing.InputVerifier {
        // TODO: should chain with potentially existing verifier in editor
        def verify(c: JComponent) = try {
          value = string2A(c.asInstanceOf[JTextField].getText)
          true
        }
        catch {
          case NonFatal(_) => false
        }
      }

      def textEditor = getEditorComponent.asInstanceOf[JTextField]
      textEditor.setInputVerifier(verifier)
      textEditor.addActionListener(Swing.ActionListener { a =>
        getItem() // make sure our value is updated
        textEditor.setText(a2String(value))
      })
    }

    override lazy val comboBoxPeer: javax.swing.ComboBoxEditor = {
      val p = comboBox.peer.asInstanceOf[JComboBox[A]]
      new DelegatedEditor(p.getEditor)
    }

    def component: Component = Component.wrap(comboBoxPeer.getEditorComponent.asInstanceOf[JComponent])
    def item: A = { comboBoxPeer.asInstanceOf[DelegatedEditor].value }
    def item_=(a: A): Unit = comboBoxPeer.setItem(a2String(a))
    def startEditing(): Unit = comboBoxPeer.selectAll()
  }

  implicit def stringEditor(c: ComboBox[String]): Editor[String] = new BuiltInEditor(c)(s => s         , s => s         )
  implicit def intEditor   (c: ComboBox[Int   ]): Editor[Int   ] = new BuiltInEditor(c)(s => s.toInt   , s => s.toString)
  implicit def floatEditor (c: ComboBox[Float ]): Editor[Float ] = new BuiltInEditor(c)(s => s.toFloat , s => s.toString)
  implicit def doubleEditor(c: ComboBox[Double]): Editor[Double] = new BuiltInEditor(c)(s => s.toDouble, s => s.toString)

  def newConstantModel[A](items: Seq[A]): Any /* ComboBoxModel */ = {
    new AbstractListModel[A] with ComboBoxModel[A] {
      private var selected: A = if (items.isEmpty) null.asInstanceOf[A] else items(0)
      def getSelectedItem: AnyRef = selected.asInstanceOf[AnyRef]
      def setSelectedItem(a: Any): Unit = {
        if ((selected != null && selected != a) ||
          selected == null && a != null) {
          selected = a.asInstanceOf[A]
          fireContentsChanged(this, -1, -1)
        }
      }
      def getElementAt(n: Int): A = items(n)
      def getSize = items.size
    }
  }

  /*def newMutableModel[A, Self](items: Seq[A] with scala.collection.mutable.Publisher[scala.collection.mutable.Message[A], Self]): ComboBoxModel = {
    new AbstractListModel with ComboBoxModel {
      private var selected = items(0)
      def getSelectedItem: AnyRef = selected.asInstanceOf[AnyRef]
      def setSelectedItem(a: Any) { selected = a.asInstanceOf[A] }
      def getElementAt(n: Int) = items(n).asInstanceOf[AnyRef]
      def getSize = items.size
    }
  }

  def newConstantModel[A](items: Seq[A]): ComboBoxModel = items match {
    case items: Seq[A] with scala.collection.mutable.Publisher[scala.collection.mutable.Message[A], Self] => newMutableModel
    case _ => newConstantModel(items)
  }*/
}

/** Lets the user make a selection from a list of predefined items. Visually,
  * this is implemented as a button-like component with a pull-down menu.
  *
  * @see javax.swing.JComboBox
  */
class ComboBox[A](items: Seq[A]) extends Component with Publisher {
  override lazy val peer: JComponent = {
    val m = ComboBox.newConstantModel(items).asInstanceOf[ComboBoxModel[A]]
    new JComboBox[A](m) with SuperMixin
  }

  object selection extends Publisher {
    def index: Int = peer.asInstanceOf[JComboBox[A]].getSelectedIndex
    def index_=(n: Int): Unit = peer.asInstanceOf[JComboBox[A]].setSelectedIndex(n)
    def item: A = peer.asInstanceOf[JComboBox[A]].getSelectedItem.asInstanceOf[A]
    def item_=(a: A): Unit = peer.asInstanceOf[JComboBox[A]].setSelectedItem(a)

    peer.asInstanceOf[JComboBox[A]].addActionListener(Swing.ActionListener { e =>
      publish(SelectionChanged(ComboBox.this))
    })
  }

  /** Sets the renderer for this combo box's items. Index -1 is
    * passed to the renderer for the selected item (not in the pull-down menu).
    *
    * The underlying combo box renders all items in a <code>ListView</code>
    * (both, in the pull-down menu as well as in the box itself), hence the
    * <code>ListView.Renderer</code>.
    *
    * Note that the UI peer of a combo box usually changes the colors
    * of the component to its own defaults _after_ the renderer has been
    * configured. That's Swing's principle of most surprise.
    */
  def renderer: ListView.Renderer[A] = ListView.Renderer.wrap[A](peer.asInstanceOf[JComboBox[A]].getRenderer)
  def renderer_=(r: ListView.Renderer[A]): Unit = {
    val rp = r.peer.asInstanceOf[ListCellRenderer[A]]
    peer.asInstanceOf[JComboBox[A]].setRenderer(rp)
  }

  /* XXX: currently not safe to expose:
  def editor: ComboBox.Editor[A] =
  def editor_=(r: ComboBox.Editor[A]) { peer.setEditor(r.comboBoxPeer) }
  */
  def editable: Boolean = peer.asInstanceOf[JComboBox[A]].isEditable

  /** Makes this combo box editable. In order to do so, this combo needs an
    * editor which is supplied by the implicit argument. For default
    * editors, see ComboBox companion object.
    */
  def makeEditable()(implicit editor: ComboBox[A] => ComboBox.Editor[A]): Unit = {
    peer.asInstanceOf[JComboBox[A]].setEditable(true)
    peer.asInstanceOf[JComboBox[A]].setEditor(editor(this).comboBoxPeer)
  }

  def prototypeDisplayValue: Option[A] = {
    val o = peer.asInstanceOf[JComboBox[A]].getPrototypeDisplayValue
    if (o == null) None else Some(o.asInstanceOf[A])
    // toOption[A](o)
  }

  def prototypeDisplayValue_=(v: Option[A]): Unit = {
    val va = if (v.isDefined) v.get else null.asInstanceOf[A]
    peer.asInstanceOf[JComboBox[A]].setPrototypeDisplayValue(va)
  }
}
