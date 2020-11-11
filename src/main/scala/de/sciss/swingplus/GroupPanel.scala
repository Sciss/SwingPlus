/*
 *  GroupPanel.scala
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

import javax.swing.{GroupLayout, LayoutStyle}
import javax.{swing => js}

import scala.language.implicitConversions
import scala.swing.{Component, Panel}

/** A panel that uses [[javax.swing.GroupLayout]] to visually arrange its components.
  *
  * __Note__: This is a slightly adapted variant of the original `GroupPanel` class
  * by Andreas Flier and which was part of the ScalaSwingContrib project.
  * We thought it was a bit over-engineered, and also some naming was
  * problematic (`theHorizontalLayout is ...`) and involving reflection-based
  * structural types.
  *
  * The key point to understanding this layout manager is that it separates
  * horizontal and vertical layout. Thus, every component appears twice: once
  * in the horizontal and once in the vertical layout. Consult the Java API
  * documentation for `GroupLayout` and Sun's Java tutorials for a
  * comprehensive explanation.
  *
  * The main advantage of using this panel instead of manually tinkering with
  * the layout is that this panel provides a concise, declarative syntax for
  * laying out its components. This approach should make most use cases easier.
  * In some special cases, e.g. when re-creating layouts on-the-fly, it might
  * be preferable to use a more imperative style, for which direct access to
  * the underlying layout manager is provided.
  *
  * In contrast to the underlying swing layout, this panel activates the
  * automatic creation of gaps between components by default, since this panel
  * is intended for coding UIs "by hand", not so much for visual UI builder
  * tools. Many features of the underlying layout are aimed at those, tough.
  * Most of them are available through this panel for completeness' sake but it
  * is anticipated that coders won't need to use them very much.
  *
  * =Code examples=
  *
  * This section contains a few simple examples to showcase the basic
  * functionality of `GroupPanel`s. For all examples, it is assumed
  * that everything from the package `scala.swing` is imported and the code is
  * placed inside a [[scala.swing.SimpleSwingApplication]] like this:
  *
  * {{{
  * import scala.swing._
  * import de.sciss.swingplus._
  *
  * object Example extends SimpleSwingApplication {
  *   lazy val top = new MainFrame {
  *     contents = new GroupPanel {
  *       // example code here
  *     }
  *   }
  * }
  * }}}
  *
  * ==Simple panel with 2 components==
  *
  * In the first example, there's a label and a text field, which appear
  * in a horizontal sequence but share the same vertical space.
  *
  * {{{
  * val label     = new Label("Label:")
  * val textField = new TextField(20)
  *
  * horizontal    = Seq(label, textField)
  * vertical      = Par(label, textField)
  * }}}
  *
  * It can be observed that the resize behaviour of the text field is rather
  * strange. To get better behaviour, the components' vertical sizes can be
  * linked together.
  *
  * {{{
  * linkVerticalSize(label, textField)
  * }}}
  *
  * Alternatively, it would have been possible to disallow the resizing of
  * the vertical, parallel group. To achieve this, the vertical layout line
  * should be written this way:
  *
  * {{{
  * vertical = Par(Leading, FixedSize)(label, textField)
  * }}}
  *
  * Since text fields aren't resizable when used with baseline alignment (more
  * about that further down), the following code also prevents (vertical)
  * resizing:
  *
  * {{{
  * vertical = Par(Baseline)(label, textField)
  * }}}
  *
  * ==Size and alignment==
  *
  * Components can be added with custom size constraints (minimum, preferred,
  * maximum size). The next example showcases that. The text field appears
  * with a preferred height of 100 pixels and when the component is resized,
  * it can be reduced to its minimum height of 50 pixels and enlarged
  * to its maximum height of 200 pixels.
  *
  * {{{
  * horizontal = Seq(label, textField)
  * vertical   = Par(label, Size(textField, 50, 100, 200))
  * }}}
  *
  * The `Size` object holds some useful constants: `Default`, `Preferred` and `Infinite`,
  * that can be used for any of the minimum, preferred and maximum arguments.
  *
  * Instead of using these hints with `Size.apply`, one can also use the
  * provided convenience methods `Size.fixed` and `Size.fill`.
  *
  * Because the default alignment in a parallel group is `Leading`,
  * both components are "glued" to the top of the container (panel). To align
  * the label's text with the text inside the text field, an explicit alignment
  * can be specified in a preceding argument list, like this:
  *
  * {{{
  * horizontal = Seq(label, textField)
  * vertical   = Par(Baseline)(label, Size(textField, 50, 100, 200))
  * }}}
  *
  * This example also shows a potential problem of baseline alignment: some
  * components stop being resizable. More specifically, the javadoc
  * for `GroupLayout.ParallelGroup` states:
  *
  *   - Elements aligned to the baseline are resizable if they have have a
  *     baseline resize behavior of `CONSTANT_ASCENT` or `CONSTANT_DESCENT`.
  *   - Elements with a baseline resize behavior of `OTHER` or `CENTER_OFFSET`
  *     are not resizable.
  *
  * Since a text field's resizing behaviour is `CENTER_OFFSET`, it is
  * not resizable when used with baseline alignment.
  *
  * ==Gaps==
  *
  * The `GroupPanel` turns on automatic creation of gaps between
  * components and along the container edges. To see the difference, try turning
  * this feature off manually by inserting the following lines:
  *
  * {{{
  * autoGaps          = false
  * autoContainerGaps = false
  * }}}
  *
  * With both types of gaps missing, the components are clamped together and to
  * the container edges, which does not look very pleasing. Gaps can be added
  * manually, too. The following example does this in order to get a result that
  * looks similar to the version with automatically created gaps, albeit in a
  * much more verbose manner.
  *
  * {{{
  * horizontal = Seq(
  *   Gap.Container(),
  *   label,
  *   Gap.Preferred(Related),
  *   textField,
  *   Gap.Container()
  * )
  *
  * vertical = Seq(
  *   Gap.Container(),
  *   Parallel(label, textField),
  *   Gap.Container()
  * )
  * }}}
  *
  * Rigid gaps with custom size or completely manual gaps (specifying minimum,
  * preferred and maximum size) between components are created with
  * the `Gap` object:
  *
  * {{{
  * bc.. horizontal = Seq(
  *   label,
  *   Gap(10, 20, 100),
  *   textField
  * )
  *
  * vertical = Seq(
  *   Par(label, Gap(30), textField)
  * )
  * }}}
  *
  * In a parallel group, such a gap can be used to specify a minimum amount of
  * space taken by the group.
  *
  * In addition to rigid gaps in the previous example, it is also possible to
  * specify gaps that resize. This could be done by specifying a maximum size
  * of `Infinite`. However, for the most commonly used type of these, there is
  * a bit of syntax sugar available with the `Spring`
  * and `ContainerSpring` methods.
  *
  * {{{
  * bc.. horizontal = Seq(
  *   Gap.Container(),
  *   label,
  *   Gap.Spring(), // default is Related
  *   textField,
  *   Gap.ContainerSpring()
  * )
  * }}}
  *
  * These create gaps that minimally are as wide as a `Gap.Preferred` would
  * be - it is possible to specify whether the `Related` or `Unrelated` distance
  * should be used - but can be resized to an arbitrary size.
  *
  * {{{
  * bc.. horizontal = Seq(
  *   Gap.Container(),
  *   label,
  *   Gap.Spring(Unrelated),
  *   textField,
  *   Gap.ContainerSpring()
  * )
  * }}}
  *
  * The preferred size can also be specified more closely (`Size.Default`
  * or `Size.Infinite` aka "as large as possible"):
  *
  * {{{
  * bc.. horizontal = Seq(
  *   Gap.Container(),
  *   label,
  *   Gap.Spring(Unrelated, Size.Infinite),
  *   textField,
  *   Gap.ContainerSpring(Size.Infinite)
  * )
  * }}}
  *
  * Please note that `Gap.Preferred`, `Spring`, `Gap.Container` and `Spring.Container` may
  * '''only''' be used inside a sequential group.
  *
  * ==A dialog with several components==
  *
  * As a last, more sophisticated example, here's the `GroupPanel`
  * version of the "Find" dialog presented as example
  * for `GroupLayout` in the Java tutorials by Sun:
  *
  * {{{
  * val label         = new Label("Find what:")
  * val textField     = new TextField
  * val caseCheckBox  = new CheckBox("Match case")
  * val wholeCheckBox = new CheckBox("Whole words")
  * val wrapCheckBox  = new CheckBox("Wrap around")
  * val backCheckBox  = new CheckBox("Search backwards")
  * val findButton    = new Button("Find")
  * val cancelButton  = new Button("Cancel")
  *
  * horizontal = Seq(
  *   label,
  *   Par(
  *     textField,
  *     Seq(
  *       Par(caseCheckBox, wholeCheckBox),
  *       Par(wrapCheckBox, backCheckBox)
  *     )
  *   ),
  *   Par(findButton, cancelButton)
  * )
  *
  * linkHorizontalSize(findButton, cancelButton)
  *
  * vertical = Seq(
  *   Par(Baseline)(label, textField, findButton),
  *   Par(
  *     Seq(
  *       Par(Baseline)(caseCheckBox, wrapCheckBox),
  *       Par(Baseline)(wholeCheckBox, backCheckBox)
  *     ),
  *     cancelButton
  *   )
  * )
  * }}}
  *
  * ==Mapping component sequences==
  *
  * Often you will want to build panels from a sequence of components and arrange them in a grid.
  * The `Seq.apply` and `Par.apply` methods take a sequence of `GroupPanel.Element` instances, and therefore
  * you may have to explicitly convert them, as the implicit conversion does not kick in for collections.
  * There are two possibilities, as demonstrated in the last example:
  *
  * {{{
  * class Param(val check: CheckBox, val label: Label, val slider: Slider, val index: Spinner)
  *
  * val p1 = new Param(
  *   new CheckBox,
  *   new Label("Foo"),
  *   new Slider { value = 10 },
  *   new Spinner(new SpinnerNumberModel(10, 0, 100, 1))
  * )
  * val p2 = new Param(
  *   new CheckBox { selected = true },
  *   new Label("Bar"),
  *   new Slider,
  *   new Spinner(new SpinnerNumberModel(50, 0, 100, 1))
  * )
  * val params = List(p1, p2)
  *
  * horizontal = Seq(
  *   Par(params.map(r => r.check: GroupPanel.Element): _*),
  *   Par(params.map(r => r.label: GroupPanel.Element): _*),
  *   new Par { params.foreach(r => contents += r.slider) },
  *   new Par { params.foreach(r => contents += r.index ) }
  * )
  *
  * vertical = Seq(
  *   params.map { p =>
  *     Par(Center)(p.check, p.label, p.slider, p.index)
  *   }: _*
  * )
  * }}}
  *
  * As can be seen, the `Seq` and `Par` classes can be instantiated and then populated through
  * calls to the `contents` member.
  *
  * @see javax.swing.GroupLayout
  * @author Hanns Holger Rutz
  * @author Andreas Flierl
  */
class GroupPanel extends Panel {
  import GroupPanel.{Alignment, Element, Group, Placement}
  
  /** This panel's underlying layout manager is a `GroupLayout` instance. */
  val layout = new GroupLayout(peer)

  peer.setLayout(layout)
  autoGaps          = true
  autoContainerGaps = true

  private[this] var _horizontalGroup: Group = _
  private[this] var _verticalGroup  : Group = _

    /** This method is needed for implicit resolution in Scala 2.10 due to some bug (?).
      * It is not needed any more for Scala 2.11
      */
  protected implicit def wrapSeq(c: Component): Element.Seq = Element(c)
  /** This method is needed for implicit resolution in Scala 2.10 due to some bug (?).
    * It is not needed any more for Scala 2.11
    */
  protected implicit def wrapPar(c: Component): Element.Par = Element(c)

  def horizontal: Group = if (_horizontalGroup != null) _horizontalGroup else
    throw new IllegalStateException("Horizontal group has not been assigned yet")
  
  def horizontal_=(value: Group): Unit = {
    _horizontalGroup = value
    layout.setHorizontalGroup(value.peer) // (value.build(layout))
  }

  def vertical: Group = if (_verticalGroup != null) _verticalGroup else
    throw new IllegalStateException("Vertical group has not been assigned yet")

  def vertical_=(value: Group): Unit = {
    _verticalGroup = value
    layout.setVerticalGroup(value.peer) // (value.build(layout))
  }

  // ---- useful "imports" ----

  // protected def Seq       = GroupPanel.Seq
  // protected def Par       = GroupPanel.Par

  protected def Leading   = Alignment.Leading
  protected def Trailing  = Alignment.Trailing
  protected def Center    = Alignment.Center
  protected def Baseline  = Alignment.Baseline

  protected def Related   = Placement.Related
  protected def Unrelated = Placement.Unrelated
  protected def Indent    = Placement.Indent

  protected def Size      = GroupPanel.Size
  protected def Gap       = GroupPanel.Gap

  // ---- layout properties ----

  /** Indicates whether gaps between components are automatically created. */
  def autoGaps                 : Boolean        = layout.getAutoCreateGaps

  /** Sets whether gaps between components are automatically created. */
  def autoGaps_=         (value: Boolean): Unit = layout.setAutoCreateGaps(value)

  /** Indicates whether gaps between components and the container borders are automatically created. */
  def autoContainerGaps        : Boolean        = layout.getAutoCreateContainerGaps

  /** Sets whether gaps between components and the container borders are automatically created. */
  def autoContainerGaps_=(value: Boolean): Unit = layout.setAutoCreateContainerGaps(value)

  /** Returns the layout style used. */
  def layoutStyle         : js.LayoutStyle       = layout.getLayoutStyle

  /** Assigns a layout style to use. */
  def layoutStyle_=(value: js.LayoutStyle): Unit = layout.setLayoutStyle(value)

  /** Indicates whether the visibility of components is considered for the layout.
    * If set to `false`, invisible components still take up space.
    * Defaults to `true`.
    */
  def honorsVisibility: Boolean = layout.getHonorsVisibility

  /** Sets whether the visibility of components should be considered for the
    * layout. If set to `false`, invisible components still take up
    * space. Defaults to `true`.
    */
  def honorsVisibility_=(value: Boolean): Unit = layout.setHonorsVisibility(value)

  // ---- layout actions ----

  /** The component will not take up any space when it's invisible (default). */
  def honorVisibilityOf (comp: Component): Unit = layout.setHonorsVisibility(comp.peer, true )

  /** The component will still take up its space even when invisible. */
  def ignoreVisibilityOf(comp: Component): Unit = layout.setHonorsVisibility(comp.peer, false)

  /** Links the sizes (horizontal and vertical) of several components.
    *
    * @param comps the components to link
    */
  def linkSize(comps: Component*): Unit = layout.linkSize(comps.map(_.peer): _*)

  /** Links the sizes of several components horizontally.
    *
    * @param comps the components to link
    */
  def linkHorizontalSize(comps: Component*): Unit =
    layout.linkSize(js.SwingConstants.HORIZONTAL, comps.map(_.peer): _*)

  /** Links the sizes of several components vertically.
    *
    * @param comps the components to link
    */
  def linkVerticalSize(comps: Component*): Unit =
    layout.linkSize(js.SwingConstants.VERTICAL, comps.map(_.peer): _*)

  /** Replaces one component with another. Useful for dynamic layouts.
    *
    * @param existing the component to be replaced
    * @param replacement the component replacing the existing one
    */
  def replace(existing: Component, replacement: Component): Unit =
    layout.replace(existing.peer, replacement.peer)

  // ---- inner classes ----

  object Seq {
    def apply(elems: Element.Seq*): Seq = {
      val res = new Seq
      res.contents ++= elems
      res
    }
  }
  class Seq extends Group {
    lazy val peer: GroupLayout#SequentialGroup = layout.createSequentialGroup

    object contents extends Growable[Element.Seq] {
      def addOne(elem: Element.Seq): this.type = {
        elem.add(layout, peer)
        this
      }

      def clear(): Unit = throw new NotImplementedError()
    }
  }

  object Par {
    def apply(elems: Element.Par*): Par = apply(Alignment.Leading)(elems: _*)

    def apply(alignment: Alignment)(elems: Element.Par*): Par = apply(alignment, resizable = true)(elems: _*)

    def apply(alignment: Alignment, resizable: Boolean)(elems: Element.Par*): Par = {
      val res = new Par(alignment, resizable = resizable)
      res.contents ++= elems
      res
    }
  }
  class Par(alignment: Alignment, resizable: Boolean) extends Group {
    def this(alignment: Alignment) =
      this(alignment, true)

    def this() =
      this(Alignment.Leading)

    lazy val peer: GroupLayout#ParallelGroup = layout.createParallelGroup(alignment, resizable)

    object contents extends Growable[Element.Par] {
      def addOne(elem: Element.Par): this.type = {
        elem.add(layout, peer)
        this
      }

      def clear(): Unit = throw new NotImplementedError()
    }
  }
}
object GroupPanel {
  sealed trait Group extends Element {
    def peer: GroupLayout#Group
    def contents: Growable[Element]

    private[GroupPanel] def add(layout: GroupLayout, parent: GroupLayout#Group): Unit = parent.addGroup(peer)
  }

  object Element {
    implicit def apply(c: Component): Element = new ComponentElement(c, None)

    /** Elements which may appear in a sequential group */
    trait Seq {
      private[GroupPanel] def add(layout: GroupLayout, parent: GroupLayout#SequentialGroup): Unit
    }
    /** Elements which may appear in a parallel group */
    trait Par {
      private[GroupPanel] def add(layout: GroupLayout, parent: GroupLayout#ParallelGroup): Unit
    }
  }

  /** Elements can appear within (sequential or parallel) groups.
    * First of all, regular Component instances can be
    * viewed as elements. Groups themselves can be nested and thus be seen as elements.
    */
  trait Element extends Element.Seq with Element.Par {
    private[GroupPanel] def add(layout: GroupLayout, parent: GroupLayout#Group): Unit

    private[GroupPanel] def add(layout: GroupLayout, parent: GroupLayout#SequentialGroup): Unit =
      add(layout, parent: GroupLayout#Group)

    private[GroupPanel] def add(layout: GroupLayout, parent: GroupLayout#ParallelGroup): Unit =
      add(layout, parent: GroupLayout#Group)
  }

  /** Represents an alignment of a component (or group) within a parallel group.
    *
    * @see javax.swing.GroupLayout.Alignment
    */
  object Alignment {
    val Leading   = GroupLayout.Alignment.LEADING
    val Trailing  = GroupLayout.Alignment.TRAILING
    val Center    = GroupLayout.Alignment.CENTER
    val Baseline  = GroupLayout.Alignment.BASELINE
  }
  type Alignment = GroupLayout.Alignment

  object Size {
    val Default   = GroupLayout.DEFAULT_SIZE
    val Preferred = GroupLayout.PREFERRED_SIZE
    val Infinite  = Int.MaxValue

    /** Specifies size constraints for this component.
      *
      * @param min minimum size >= 0 (or one of `UseDefault`, `UsePreferred` and `Infinite`)
      * @param pref preferred size >= 0 (or one of `UseDefault`, `UsePreferred` and `Infinite`)
      * @param max maximum size >= 0 (or one of `UseDefault`, `UsePreferred` and `Infinite`)
      */
    def apply(comp: Component, min: Int, pref: Int, max: Int): Element =
      new ComponentElement(comp, Some(new Sizes(min = min, pref = pref, max = max)))

    /** Fixes the size of this component to a given or its default size.
      * That is, both minimum and maximum size are set to the given preferred size.
     *
     * @param comp  the component to constrain
     * @param size  the preferred size. defaults to `Default`
     */
    def fixed(comp: Component, size: Int = Default): Element =
      apply(comp, min = Preferred, pref = size, max = Preferred)

    /** Forces this component to be resizable (useful e.g. for buttons).
      *
      * @param comp the component to constrain
      * @param min  the minimum size. defaults to `Default`
      * @param pref the preferred size. defaults to `Default`
      * @param max  the maximum size. defaults to `Infinite`
      */
    def fill(comp: Component, min: Int = Default, pref: Int = Default, max: Int = Infinite): Element =
      apply(comp, min = min, pref = pref, max = max)
  }

  object Gap {
    def Container(pref: Int = Size.Default, max: Int = Size.Default): Element.Seq = new ContainerGap(pref, max)
    def Preferred(placement: Placement, pref: Int = Size.Default, max: Int = Size.Default): Element.Seq =
      new PreferredGap(placement, pref, max)

    def apply(size: Int): Element = apply(size, size, size)
    def apply(min: Int, pref: Int, max: Int): Element = new GapImpl(min, pref, max)

    def Spring(placement: Placement = Placement.Related, pref: Int = Size.Default): Element.Seq =
      Preferred(placement, pref, Size.Infinite)

    def ContainerSpring(pref: Int = Size.Default): Element.Seq = Container(pref, Size.Infinite)
  }

  object Placement {
    val Related   = LayoutStyle.ComponentPlacement.RELATED
    val Unrelated = LayoutStyle.ComponentPlacement.UNRELATED
    val Indent    = LayoutStyle.ComponentPlacement.INDENT
  }
  type Placement = LayoutStyle.ComponentPlacement

  // -------------------------
  // -------------------------
  // ---- implementations ----
  // -------------------------
  // -------------------------

  private[this] final class GapImpl(min: Int, pref: Int, max: Int) extends Element {
    override def toString: String = if (min == pref && max == pref)
      s"Gap($pref)"
    else
      s"Gap($min, $pref, $max)"

    def add(layout: GroupLayout, parent: GroupLayout#Group): Unit =
      parent.addGap(min, pref, max)
  }

  private[this] final class ContainerGap(pref: Int, max: Int) extends Element.Seq {
    override def toString = s"Gap.Container($pref, $max)"

    def add(layout: GroupLayout, parent: GroupLayout#SequentialGroup): Unit =
      parent.addContainerGap(pref, max)
  }

  private[this] final class PreferredGap(placement: Placement, pref: Int, max: Int) extends Element.Seq {
    override def toString: String = {
      val placeStr = (placement: @unchecked) match {  // scalac doesn't handle aliasing
        case Placement.Related    => "Related"
        case Placement.Unrelated  => "Unrelated"
        case Placement.Indent     => "Indent"
      }
      val szString = if (pref == Size.Default && max == Size.Default) "" else s", $pref, $max"
      s"Gap.Preferred($placeStr$szString)"
    }

    def add(layout: GroupLayout, parent: GroupLayout#SequentialGroup): Unit =
      parent.addPreferredGap(placement, pref, max)
  }

  private[this] final class Sizes(val min: Int, val pref: Int, val max: Int) {
    override def toString = s"{ min: $min, pref: $pref, max: $max }"
  }

  private[this] final class ComponentElement(c: Component, sizes: Option[Sizes]) extends Element {
    override def toString = s"$c${sizes.fold("")(sz => s" $sz")}"

    def add(layout: GroupLayout, parent: GroupLayout#Group): Unit = sizes.fold {
      parent.addComponent(c.peer)
    } { sz =>
      parent.addComponent(c.peer, sz.min, sz.pref, sz.max)
    }
  }
}