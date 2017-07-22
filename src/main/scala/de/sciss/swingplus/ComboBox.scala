/*
 *  ComboBox.scala
 *  (SwingPlus)
 *
 *  Copyright (c) 2013-2017 Hanns Holger Rutz. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

// This is a version of the original Scala-Swing class, adapted to
// compile under Java 7, and capable of being used in projects that
// compile with either Java 6 and 7. In other words, the idiotic
// generification of Java-Swing under Java 7 is hidden. The code
// has further been cleaned up, the API made more regular, and
// a proper Model type has been introduced.

package de.sciss.swingplus

/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2007-2013, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

import javax.swing.event.{ListDataListener, ListDataEvent}
import javax.swing.{ListCellRenderer, JComponent, JComboBox, JTextField, ComboBoxModel, AbstractListModel}
import java.awt.event.ActionListener

import de.sciss.swingplus.ComboBox.Model

import scala.swing.event.{Event, SelectionChanged, ActionEvent}
import scala.swing.{Swing, Component, Reactions, Publisher}
import scala.util.control.NonFatal
import scala.collection.mutable

import scala.language.implicitConversions

object ComboBox {
  // ------------------------- Editor -------------------------

  /** An editor for a combo box. Lets you edit the currently selected item.
    * It is highly recommended to use the BuiltInEditor class. For anything
    * else, one cannot guarantee that it integrates nicely with the current
    * look-and-feel.
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
    var item: A

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

  // ------------------------- Model-------------------------

  object Model {
    def wrap[A](items: Seq[A]): Model[A] = new Wrapped(items)

    def empty[A]: Model[A] with mutable.Buffer[A] = new BufferImpl[A]

    private final class BufferImpl[A] extends Model[A] with mutable.Buffer[A] { m =>
      private val peer = mutable.Buffer.empty[A]

      override def toString() = s"ComboBox.Model@${hashCode().toHexString}"

      private var _selected = Option.empty[A]
      def selectedItem: Option[A] = _selected
      def selectedItem_=(a: Option[A]): Unit = if (_selected != a) {
        _selected = a
        publish(Model.SelectionChanged(m))
      }

      def apply(n: Int): A = peer.apply(n)
      def length: Int = peer.length
      def iterator: Iterator[A] = peer.iterator

      def update(n: Int, newElem: A): Unit = if (peer(n) != newElem) {
        peer.update(n, newElem)
        publish(Model.ElementsChanged(m, n to n))
      }

      def clear(): Unit = if (peer.nonEmpty) {
        selectedItem = None
        peer.clear()
        publish(Model.ElementsRemoved(m, 0 until peer.size))
      }

      def remove(n: Int): A = {
        if (Some(n) == selectedItem) {
          val idx = if (n == 0) 1 else n - 1
          selectedItem = if (idx >= 0 && idx < size) Some(apply(idx)) else None
        }
        val res = peer.remove(n)
        publish(Model.ElementsRemoved(m, n to n))
        res
      }

      def +=: (elem: A): this.type = {
        val wasEmpty = isEmpty
        peer.+=:(elem)
        publish(Model.ElementsAdded(m, 0 to 0))
        if (wasEmpty) selectedItem = Some(elem)
        this
      }

      def += (elem: A): this.type = {
        val n = peer.size
        val wasEmpty = n == 0
        peer += elem
        publish(Model.ElementsAdded(m, n to n))
        if (wasEmpty) selectedItem = Some(elem)
        this
      }

      def insertAll(n: Int, elems: Traversable[A]): Unit = {
        val wasEmpty = isEmpty
        peer.insertAll(n, elems)
        publish(Model.ElementsAdded(m, n to (n + elems.size)))
        if (wasEmpty) selectedItem = headOption
      }
    }

    private[ComboBox] final class Wrapped[A](val items: Seq[A]) extends Model[A] { m =>
      def length: Int = items.length
      def apply(idx: Int): A = items.apply(idx)
      def iterator: Iterator[A] = items.iterator

      private var _selected = items.headOption
      def selectedItem: Option[A] = _selected
      def selectedItem_=(a: Option[A]): Unit = if (_selected != a) {
        _selected = a
        publish(Model.SelectionChanged(m))
      }

      override def toString() = s"ListView.Model.wrap($items)"
    }

    // creates a Scala model from an existing underlying Java model
    private[ComboBox] final class FromJava[A](val peer: JComponent) extends Model[A] with LazyPublisher { m =>
      private val pm: ComboBoxModel[A] = peer.asInstanceOf[JComboBox[A]].getModel

      override def toString() = s"ListView.Model@${hashCode().toHexString}"

      def length: Int = pm.getSize
      def apply(idx: Int): A = pm.getElementAt(idx)
      def iterator: Iterator[A] = new Iterator[A] {
        private var idx = 0
        def hasNext: Boolean = idx < pm.getSize
        def next(): A = {
          val res = pm.getElementAt(idx)
          idx += 1
          res
        }
      }

      def selectedItem: Option[A] = Option(pm.getSelectedItem).asInstanceOf[Option[A]]

      def selectedItem_=(a: Option[A]): Unit = pm.setSelectedItem(a.asInstanceOf[Option[AnyRef]].orNull)

      private[this] lazy val l: ListDataListener = new ListDataListener {
        def contentsChanged(e: ListDataEvent): Unit = {
          val evt = if (e.getIndex0 < 0)
            Model.SelectionChanged(m)
          else
            Model.ElementsChanged(m, e.getIndex0 to e.getIndex1)

          m.publish(evt)
        }

        def intervalRemoved(e: ListDataEvent): Unit = m.publish(Model.ElementsRemoved(m, e.getIndex0 to e.getIndex1))
        def intervalAdded  (e: ListDataEvent): Unit = m.publish(Model.ElementsAdded  (m, e.getIndex0 to e.getIndex1))
      }

      protected def onFirstSubscribe (): Unit = pm.addListDataListener   (l)
      protected def onLastUnsubscribe(): Unit = pm.removeListDataListener(l)
    }

    // creates a Java model from an existing underlying Scala model
    private[ComboBox] final class ToJava[A](val peer: Model[A]) extends AbstractListModel[A] with ComboBoxModel[A] {
      def getElementAt(n: Int): A = peer.apply(n)
      def getSize: Int = peer.length

      peer.reactions += {
        case Model.ElementsChanged(m, range) => fireContentsChanged(m, range.start, range.last)
        case Model.ElementsAdded  (m, range) => fireIntervalAdded  (m, range.start, range.last)
        case Model.ElementsRemoved(m, range) => fireIntervalRemoved(m, range.start, range.last)
        case Model.SelectionChanged(m)       => fireContentsChanged(m, -1, -1)
      }

      def setSelectedItem(a: AnyRef): Unit = peer.selectedItem = Option(a).asInstanceOf[Option[A]]

      def getSelectedItem: AnyRef = peer.selectedItem.asInstanceOf[Option[AnyRef]].orNull
    }

    // ------------------------- Events -------------------------

    type Change[+A]           = ListView.Model.Change[A]
    type ElementsChanged[+A]  = ListView.Model.ElementsChanged[A]
    val  ElementsChanged      = ListView.Model.ElementsChanged
    type ElementsAdded[+A]    = ListView.Model.ElementsAdded[A]
    val  ElementsAdded        = ListView.Model.ElementsAdded
    type ElementsRemoved[+A]  = ListView.Model.ElementsRemoved[A]
    val  ElementsRemoved      = ListView.Model.ElementsRemoved

    final case class SelectionChanged[A](model: Model[A]) extends Event
  }
  trait Model[A] extends ListView.Model[A] {
    var selectedItem: Option[A]
  }
}

/** Lets the user make a selection from a list of predefined items. Visually,
  * this is implemented as a button-like component with a pull-down menu.
  *
  * @see javax.swing.JComboBox
  */
class ComboBox[A] extends Component with Publisher {
  override lazy val peer: JComponent = new JComboBox[A] with SuperMixin

  def this(model: ComboBox.Model[A]) = {
    this()
    setModel(model)
  }

  def this(items: Seq[A]) = {
    this()
    this.items = items
  }

  private[this] var _model: Model[A] = null

  //  private[this] val modelListener: Reaction = {
  //    case Model.ElementsChanged(_, _    )  => publish(ListChanged        (ListView.this       ))
  //    case Model.ElementsAdded  (_, range)  => publish(ListElementsRemoved(ListView.this, range))
  //    case Model.ElementsRemoved(_, range)  => publish(ListElementsAdded  (ListView.this, range))
  //  }

  private[this] def setModel(m: Model[A]): Unit = {
    // if (_model != null) _model.reactions -= modelListener

    val jm = m match {
      case i: Model.FromJava[A] => i.peer.asInstanceOf[JComboBox[A]].getModel
      case _ => new Model.ToJava(m)
    }
    peer.asInstanceOf[JComboBox[A]].setModel(jm)
    // m.reactions += modelListener
    _model = m
  }

  def model: Model[A] = {
    if (_model == null) setModel(new Model.FromJava[A](peer))
    _model
  }

  def model_=(value: Model[A]): Unit = if (_model != value) {
    setModel(value)
    // publish(ListChanged(ListView.this))
  }

  def items: Seq[A] = model match {
    case mw: Model.Wrapped[A] => mw.items
    case m => m
  }

  def items_=(xs: Seq[A]): Unit = model = xs match {
    case m: Model[A] => m
    case _ => Model.wrap(xs)
  }

  object selection extends Publisher {
    def index    : Int        = peer.asInstanceOf[JComboBox[A]].getSelectedIndex
    def index_=(n: Int): Unit = peer.asInstanceOf[JComboBox[A]].setSelectedIndex(n)

    def item     : A          = peer.asInstanceOf[JComboBox[A]].getSelectedItem.asInstanceOf[A]
    def item_= (a: A  ): Unit = peer.asInstanceOf[JComboBox[A]].setSelectedItem(a)

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
