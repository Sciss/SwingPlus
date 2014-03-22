package de.sciss.swingplus

import scala.swing.{Slider, CheckBox, FlowPanel, Button, Component, MainFrame, TextField, Label, Frame, SimpleSwingApplication}
import scala.annotation.switch
import javax.swing.SpinnerNumberModel

object GroupPanelTest extends SimpleSwingApplication {
  private def mkEx(i: Int): Component = Button(i.toString) {
    val ex = (i: @switch) match {
      case 1 => ex1()
      case 2 => ex2()
      case 3 => ex3()
      case 4 => ex4()
      case 5 => ex5()
      case 6 => ex6()
      case 7 => ex7()
      case 8 => ex8()
      case 9 => ex9()
      case 10 => ex10()
      case 11 => ex11()
      case 12 => ex12()
    }
    new Frame {
      title     = s"Ex. $i"
      contents  = ex
      pack().centerOnScreen()
      open()
    }
  }

  lazy val top: Frame = new MainFrame {
    title     = "Group Panel"
    contents  = new FlowPanel((1 to 12).map(mkEx): _*)
  }

  private def ex1(): MyPanel = new MyPanel {
    horizontal  = Seq(label, textField)
    vertical    = Par(label, textField)

    linkVerticalSize(label, textField)
  }

  private def ex2(): Component = new MyPanel {
    horizontal  = Seq(label, textField)
    vertical    = Par(Leading, resizable = false)(label, textField)
  }

  private def ex3(): Component = new MyPanel {
    horizontal  = Seq(label, textField)
    vertical    = Par(Baseline)(label, textField)
  }

  private def ex4(): Component = new MyPanel {
    horizontal  = Seq(label, textField)
    vertical    = Par(label, Size(textField, 50, 100, 200))
  }

  private def ex5(): Component = new MyPanel {
    horizontal  = Seq(label, textField)
    vertical    = Par(Baseline)(label, Size(textField, 50, 100, 200))
  }

  private def ex6(): Component = {
    val p = ex1()
    p.autoGaps          = false
    p.autoContainerGaps = false
    p
  }

  private def ex7(): Component = new MyPanel {
    autoGaps          = false
    autoContainerGaps = false
    horizontal = Seq(
      Gap.Container(),
      label,
      Gap.Preferred(Related),
      textField,
      Gap.Container()
    )
    vertical = Seq(
      Gap.Container(),
      Par(Baseline)(label, textField),
      Gap.Container()
    )
  }

  private def ex8(): Component = new MyPanel {
    autoGaps          = false
    autoContainerGaps = false
    horizontal = Seq(
      label,
      Gap(10, 20, 100),
      textField
    )
    vertical = Seq(
      Par(Baseline)(label, Gap(30), textField)
    )
  }

  private def ex9(place: GroupPanel.Placement = GroupPanel.Placement.Related): Component = new MyPanel {
    autoGaps          = false
    autoContainerGaps = false
    horizontal = Seq(
      Gap.Container(),
      label,
      Gap.Spring(place),
      textField,
      Gap.ContainerSpring()
    )
    vertical = Seq(
      Par(Baseline)(label, Gap(30), textField)
    )
  }

  private def ex10(): Component = ex9(GroupPanel.Placement.Unrelated)

  private def ex11(): Component = new GroupPanel {
    val label         = new Label("Find what:")
    val textField     = new TextField
    val caseCheckBox  = new CheckBox("Match case")
    val wholeCheckBox = new CheckBox("Whole words")
    val wrapCheckBox  = new CheckBox("Wrap around")
    val backCheckBox  = new CheckBox("Search backwards")
    val findButton    = new Button("Find")
    val cancelButton  = new Button("Cancel")

    horizontal = Seq(
      label,
      Par(
        textField,
        Seq(
          Par(caseCheckBox, wholeCheckBox),
          Par(wrapCheckBox, backCheckBox)
        )
      ),
      Par(findButton, cancelButton)
    )
    linkHorizontalSize(findButton, cancelButton)

    vertical = Seq(
      Par(Baseline)(label, textField, findButton),
      Par(
        Seq(
          Par(Baseline)(caseCheckBox, wrapCheckBox),
          Par(Baseline)(wholeCheckBox, backCheckBox)
        ),
        cancelButton
      )
    )
  }

  private class Reduction(val norm: CheckBox, val nameLabel: Label, val slider: Slider, val index: Spinner,
                          val valueLabel: Label)

  private def ex12(): Component = new GroupPanel {
    val red1 = new Reduction(
      new CheckBox,
      new Label("Foo"),
      new Slider,
      new Spinner(new SpinnerNumberModel(10, 0, 100, 1)),
      new Label("x")
    )
    val red2 = new Reduction(
      new CheckBox { selected = true },
      new Label("Bar"),
      new Slider { value = 50 },
      new Spinner(new SpinnerNumberModel(50, 0, 100, 1)),
      new Label("y")
    )
    val red3 = new Reduction(
      new CheckBox,
      new Label("Blah Baz"),
      new Slider { value = 100 },
      new Spinner(new SpinnerNumberModel(100, 0, 150, 1)),
      new Label("z")
    )
    val reds = List(red1, red2, red3)

    ???
//    horizontal = Seq(
//      Par(reds.map(_.norm      ): _*),
//      Par(reds.map(_.nameLabel ): _*),
//      Par(reds.map(_.slider    ): _*),
//      Par(reds.map(_.index     ): _*),
//      Par(reds.map(_.valueLabel): _*)
//    )

    vertical = Seq(
      reds.map { r =>
        Par(Center)(r.norm, r.nameLabel, r.slider, r.index, r.valueLabel)
      }: _*
    )
  }

  private class MyPanel extends GroupPanel {
    val label     = new Label("Label:")
    val textField = new TextField(20)
  }
}
