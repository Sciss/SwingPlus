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

import javax.swing.event.{ListSelectionListener, ListDataEvent, ListDataListener}
import javax.swing.{JLabel, DefaultListCellRenderer, AbstractListModel, JList, ListCellRenderer, JComponent, ListSelectionModel}

import de.sciss.swingplus.event.{ListSelectionChanged, ListElementsAdded, ListElementsRemoved, ListChanged}

import scala.collection.mutable
import scala.swing.Reactions.Reaction
import scala.swing.{Label, Color, Publisher, Component}

object ListView {
  /** The supported modes of user selections. */
  object IntervalMode extends Enumeration {
    val Single         = Value(ListSelectionModel.SINGLE_SELECTION)
    val SingleInterval = Value(ListSelectionModel.SINGLE_INTERVAL_SELECTION)
    val MultiInterval  = Value(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
  }

  def wrap[A](c: JComponent): ListView[A] = new ListView[A] {
    override lazy val peer = c
  }

  // ------------------------- Model-------------------------

  object Model {
    def wrap[A](items: Seq[A]): Model[A] = new Wrapped(items)

    def empty[A]: Model[A] with mutable.Buffer[A] = new BufferImpl[A]

    private final class BufferImpl[A] extends Model[A] with mutable.Buffer[A] { m =>
      private val peer = mutable.Buffer.empty[A]

      override def toString() = s"ListView.Model@${hashCode().toHexString}"

      def apply(n: Int): A = peer.apply(n)
      def length: Int = peer.length
      def iterator: Iterator[A] = peer.iterator

      def update(n: Int, newElem: A): Unit = if (peer(n) != newElem) {
        peer.update(n, newElem)
        publish(Model.Changed(m, n to n))
      }

      def clear(): Unit = if (peer.nonEmpty) {
        peer.clear()
        publish(Model.ElementsRemoved(m, 0 until peer.size))
      }

      def remove(n: Int): A = {
        val res = peer.remove(n)
        publish(Model.ElementsRemoved(m, n to n))
        res
      }

      def +=: (elem: A): this.type = {
        peer.+=:(elem)
        publish(Model.ElementsAdded(m, 0 to 0))
        this
      }

      def += (elem: A): this.type = {
        val n = peer.size
        peer += elem
        publish(Model.ElementsAdded(m, n to n))
        this
      }

      def insertAll(n: Int, elems: Traversable[A]): Unit = {
        peer.insertAll(n, elems)
        publish(Model.ElementsAdded(m, n to (n + elems.size)))
      }
    }
    
    private[ListView] final class Wrapped[A](val items: Seq[A]) extends Model[A] {
      def length: Int = items.length
      def apply(idx: Int): A = items.apply(idx)
      def iterator: Iterator[A] = items.iterator

      override def toString() = s"ListView.Model.wrap($items)"
    }

    // creates a Scala model from an existing underlying Java model
    private[ListView] final class FromJava[A](val peer: JComponent) extends Model[A] with LazyPublisher { m =>
      private val pm = peer.asInstanceOf[JList[A]].getModel

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

      private[this] lazy val l: ListDataListener = new ListDataListener {
        def contentsChanged(e: ListDataEvent): Unit = m.publish(Model.Changed        (m, e.getIndex0 to e.getIndex1))
        def intervalRemoved(e: ListDataEvent): Unit = m.publish(Model.ElementsRemoved(m, e.getIndex0 to e.getIndex1))
        def intervalAdded  (e: ListDataEvent): Unit = m.publish(Model.ElementsAdded  (m, e.getIndex0 to e.getIndex1))
      }

      protected def onFirstSubscribe (): Unit = pm.addListDataListener   (l)
      protected def onLastUnsubscribe(): Unit = pm.removeListDataListener(l)
    }

    // creates a Java model from an existing underlying Scala model
    private[ListView] final class ToJava[A](val peer: Model[A]) extends AbstractListModel[A] {
      def getElementAt(n: Int): A = peer.apply(n)
      def getSize: Int = peer.length

      peer.reactions += {
        case Model.Changed        (m, range) => fireContentsChanged(m, range.start, range.last)
        case Model.ElementsAdded  (m, range) => fireIntervalAdded  (m, range.start, range.last)
        case Model.ElementsRemoved(m, range) => fireIntervalRemoved(m, range.start, range.last)
      }
    }

    // ------------------------- Events -------------------------

    sealed trait Change[+A] extends scala.swing.event.Event {
      def source: Model[A]
      def range: Range
    }

    final case class Changed        [+A](source: Model[A], range: Range) extends Change[A]
    final case class ElementsAdded  [+A](source: Model[A], range: Range) extends Change[A]
    final case class ElementsRemoved[+A](source: Model[A], range: Range) extends Change[A]
  }
  trait Model[+A] extends Seq[A] with Publisher

  // ------------------------- Renderer -------------------------

  object Renderer {
    def wrap[A](r: Any): Renderer[A] = new Wrapped[A](r)

    /** Wrapper for <code>javax.swing.ListCellRenderer<code>s */
    protected class Wrapped[A](override val peer: Any) extends Renderer[A] {
      def componentFor(list: ListView[_], isSelected: Boolean, focused: Boolean, a: A, index: Int): Component = {
        val p   = peer.asInstanceOf[ListCellRenderer[A]]
        val lp  = list.peer.asInstanceOf[JList[A]]
        Component.wrap(p.getListCellRendererComponent(lp, a, index, isSelected, focused).asInstanceOf[JComponent])
      }
    }

    /** Returns a renderer for items of type <code>A</code>. The given function
      * converts items of type <code>A</code> to items of type <code>B</code>
      * for which a renderer is implicitly given. This allows chaining of
      * renderers, e.g.:
      *
      * <code>
      * case class Person(name: String, email: String)
      * val persons = List(Person("John", "j.doe@a.com"), Person("Mary", "m.jane@b.com"))
      * new ListView(persons) {
      * renderer = ListView.Renderer(_.name)
      * }
      * </code>
      */
    def apply[A,B](f: A => B)(implicit renderer: Renderer[B]): Renderer[A] = new Renderer[A] {
      def componentFor(list: ListView[_], isSelected: Boolean, focused: Boolean, a: A, index: Int): Component =
        renderer.componentFor(list, isSelected, focused, f(a), index)
    }
  }

  /** Item renderer for a list view. This is contravariant on the type of the
    * items, so a more general renderer can be used in place of a more specific
    * one. For instance, an <code>Any</code> renderer can be used for a list view
    * of strings.
    *
    * @see javax.swing.ListCellRenderer
    */
  abstract class Renderer[-A] {
    def peer: Any = new ListCellRenderer[A] {
      def getListCellRendererComponent(list: JList[_ <: A], a: A, index: Int, isSelected: Boolean, focused: Boolean) =
        componentFor(ListView.wrap[A](list), isSelected, focused, a, index).peer
    }

    def componentFor(list: ListView[_], isSelected: Boolean, focused: Boolean, a: A, index: Int): Component
  }

  /** A default renderer that maintains a single component for item rendering
    * and preconfigures it to sensible defaults. It is polymorphic on the
    * component's type so clients can easily use component specific attributes
    * during configuration.
    */
  abstract class AbstractRenderer[-A, C <: Component](protected val component: C) extends Renderer[A] {
    // The renderer component is responsible for painting selection
    // backgrounds. Hence, make sure it is opaque to let it draw
    // the background.
    component.opaque = true

    /** Standard preconfiguration that is commonly done for any component.
      * This includes foreground and background colors, as well as colors
      * of item selections.
      */
    def preConfigure(list: ListView[_], isSelected: Boolean, focused: Boolean, a: A, index: Int): Unit =
      if (isSelected) {
        component.background = list.selectionBackground
        component.foreground = list.selectionForeground
      } else {
        component.background = list.background
        component.foreground = list.foreground
      }

    /** Configuration that is specific to the component and this renderer. */
    def configure(list: ListView[_], isSelected: Boolean, focused: Boolean, a: A, index: Int): Unit

    /** Configures the component before returning it. */
    def componentFor(list: ListView[_], isSelected: Boolean, focused: Boolean, a: A, index: Int): Component = {
      preConfigure(list, isSelected, focused, a, index)
      configure(list, isSelected, focused, a, index)
      component
    }
  }

  /** A generic renderer that uses Swing's built-in renderers. If there is no
    * specific renderer for a type, this renderer falls back to a renderer
    * that renders the string returned from an item's <code>toString</code>.
    */
  implicit object GenericRenderer extends Renderer[Any] {
    override lazy val peer: Any = new DefaultListCellRenderer
    def componentFor(list: ListView[_], isSelected: Boolean, focused: Boolean, a: Any, index: Int): Component = {
      val p   = peer.asInstanceOf[DefaultListCellRenderer]
      val lp  = list.peer.asInstanceOf[JList[_]]
      val c   = p.getListCellRendererComponent(lp, a, index, isSelected, focused).asInstanceOf[JComponent]
      Component.wrap(c)
    }
  }

  // ---- the first useful thing around here ----

  abstract class LabelRenderer[-A] extends Renderer[A] { r =>
    override lazy val peer: Any = new DefaultListCellRenderer

    protected lazy val component: Label = new Label {
      override lazy val peer: JLabel = r.peer.asInstanceOf[DefaultListCellRenderer]
    }

    /** Configuration that is specific to the component and this renderer. */
    def configure(list: ListView[_], isSelected: Boolean, focused: Boolean, a: A, index: Int): Unit

    /** Configures the component before returning it. */
    def componentFor(list: ListView[_], isSelected: Boolean, focused: Boolean, a: A, index: Int): Component = {
      val p   = peer.asInstanceOf[DefaultListCellRenderer]
      val lp  = list.peer.asInstanceOf[JList[_]]
      /* val c = */ p.getListCellRendererComponent(lp, a, index, isSelected, focused) // .asInstanceOf[JComponent]
      configure(list, isSelected, focused, a, index)
      component
    }
  }
}

/** A component that displays a number of elements in a list. A list view does
  * not support inline editing of items. If you need it, use a table view instead.
  *
  * Named <code>ListView</code> to avoid a clash with the frequently used
  * <code>scala.List</code>
  *
  * @see javax.swing.JList
  */
class ListView[A] extends Component {
  import ListView._
  override lazy val peer: JComponent = new JList[A] with SuperMixin

  def this(model: ListView.Model[A]) = {
    this()
    setModel(model)
  }

  def this(items: Seq[A]) = {
    this()
    listData = items
  }

  private[this] var _model: Model[A] = null

  private[this] val modelListener: Reaction = {
    case Model.Changed        (_, _    )  => publish(ListChanged        (ListView.this       ))
    case Model.ElementsAdded  (_, range)  => publish(ListElementsRemoved(ListView.this, range))
    case Model.ElementsRemoved(_, range)  => publish(ListElementsAdded  (ListView.this, range))
  }

  private[this] def setModel(m: Model[A]): Unit = {
    if (_model != null) _model.reactions -= modelListener

    val jm = m match {
      case i: Model.FromJava[A] => i.peer.asInstanceOf[JList[A]].getModel
      case _ => new Model.ToJava(m)
    }
    peer.asInstanceOf[JList[A]].setModel(jm)
    m.reactions += modelListener
    _model = m
  }

  def model: Model[A] = {
    if (_model == null) setModel(new Model.FromJava[A](peer))
    _model
  }

  def model_=(value: Model[A]): Unit = if (_model != value) {
    setModel(value)
    publish(ListChanged(ListView.this))
  }

  def listData: Seq[A] = model match {
    case mw: Model.Wrapped[A] => mw.items
    case m => m
  }

  def listData_=(items: Seq[A]): Unit = model = items match {
    case m: Model[A] => m
    case _ => Model.wrap(items)
  }

  /** The current item selection. */
  object selection extends Publisher {
    protected abstract class Indices[B](a: => Seq[B]) extends scala.collection.mutable.Set[B] {
      def -=(n: B): this.type
      def +=(n: B): this.type
      def contains(n: B): Boolean = a.contains(n)
      override def size: Int = a.length
      def iterator: Iterator[B] = a.iterator
    }

    def leadIndex  : Int = peer.asInstanceOf[JList[A]].getSelectionModel.getLeadSelectionIndex
    def anchorIndex: Int = peer.asInstanceOf[JList[A]].getSelectionModel.getAnchorSelectionIndex

    /** The indices of the currently selected items. */
    object indices extends Indices (peer.asInstanceOf[JList[A]].getSelectedIndices) {
      def -=(n: Int): this.type = { peer.asInstanceOf[JList[A]].removeSelectionInterval(n,n); this }
      def +=(n: Int): this.type = { peer.asInstanceOf[JList[A]].addSelectionInterval   (n,n); this }
    }

    /** The currently selected items. */
    object items extends scala.collection.SeqProxy[A] {
      def self: Seq[A] = {
        val p = peer.asInstanceOf[JList[A]]
        p.getSelectedValues.map(_.asInstanceOf[A])    // deprecated, but not available in Java 6!
      }
    }

    def intervalMode: IntervalMode.Value = IntervalMode(peer.asInstanceOf[JList[A]].getSelectionModel.getSelectionMode)
    def intervalMode_=(m: IntervalMode.Value): Unit = peer.asInstanceOf[JList[A]].getSelectionModel.setSelectionMode(m.id)

    peer.asInstanceOf[JList[A]].getSelectionModel.addListSelectionListener(new ListSelectionListener {
      def valueChanged(e: javax.swing.event.ListSelectionEvent): Unit =
        publish(new ListSelectionChanged(ListView.this, e.getFirstIndex to e.getLastIndex, e.getValueIsAdjusting))
    })

    def adjusting: Boolean = peer.asInstanceOf[JList[A]].getSelectionModel.getValueIsAdjusting
  }

  def renderer: ListView.Renderer[A] = ListView.Renderer.wrap[A](peer.asInstanceOf[JList[A]].getCellRenderer)
  def renderer_=(r: ListView.Renderer[A]): Unit = {
    val rp = r.peer.asInstanceOf[ListCellRenderer[A]]
    peer.asInstanceOf[JList[A]].setCellRenderer(rp)
  }

  def fixedCellWidth: Int = peer.asInstanceOf[JList[A]].getFixedCellWidth
  def fixedCellWidth_=(x: Int): Unit = peer.asInstanceOf[JList[A]].setFixedCellWidth(x)

  def fixedCellHeight: Int = peer.asInstanceOf[JList[A]].getFixedCellHeight
  def fixedCellHeight_=(x: Int): Unit = peer.asInstanceOf[JList[A]].setFixedCellHeight(x)

  def prototypeCellValue: A = peer.asInstanceOf[JList[A]].getPrototypeCellValue
  def prototypeCellValue_=(a: A): Unit = peer.asInstanceOf[JList[A]].setPrototypeCellValue(a)

  def visibleRowCount: Int = peer.asInstanceOf[JList[A]].getVisibleRowCount
  def visibleRowCount_=(n: Int): Unit = peer.asInstanceOf[JList[A]].setVisibleRowCount(n)

  def ensureIndexIsVisible(idx: Int): Unit = peer.asInstanceOf[JList[A]].ensureIndexIsVisible(idx)

  def selectionForeground: Color = peer.asInstanceOf[JList[A]].getSelectionForeground
  def selectionForeground_=(c: Color): Unit = peer.asInstanceOf[JList[A]].setSelectionForeground(c)
  def selectionBackground: Color = peer.asInstanceOf[JList[A]].getSelectionBackground
  def selectionBackground_=(c: Color): Unit = peer.asInstanceOf[JList[A]].setSelectionBackground(c)

  def selectIndices(ind: Int*): Unit = peer.asInstanceOf[JList[A]].setSelectedIndices(ind.toArray)

  // ---- additions ----

  def dragEnabled        : Boolean               = peer.asInstanceOf[JList[A]].getDragEnabled
  def dragEnabled_=(value: Boolean): Unit        = peer.asInstanceOf[JList[A]].setDragEnabled(value)
  def dropMode           : DropMode.Value        = peer.asInstanceOf[JList[A]].getDropMode
  def dropMode_=   (value: DropMode.Value): Unit = peer.asInstanceOf[JList[A]].setDropMode(value)
}
