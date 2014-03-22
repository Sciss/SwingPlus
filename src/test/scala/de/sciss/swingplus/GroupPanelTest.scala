package de.sciss.swingplus

import scala.swing.{Slider, CheckBox, FlowPanel, Button, Component, MainFrame, TextField, Label, Frame, SimpleSwingApplication}
import scala.annotation.switch
import javax.swing.SpinnerNumberModel
import scala.swing.event.{ValueChanged, ButtonClicked}

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


  private def ex12(): Component = new GroupPanel {
    class Param(val check: CheckBox, val label: Label, val slider: Slider, val index: Spinner) {
      private def updateEnabled(): Unit = {
        slider.enabled = check.selected
        index.enabled  = check.selected
      }

      updateEnabled()
      check.listenTo(check)
      check.reactions += {
        case ButtonClicked(_) => updateEnabled()
      }

      slider.listenTo(slider)
      slider.reactions += {
        case ValueChanged(_) =>
          index.deafTo(index)
          println(s"Slider ${slider.value}")
          index.value = slider.value
          index.listenTo(index)
      }

      index.listenTo(index)
      index.reactions += {
        case ValueChanged(_) => index.value match {
          case i: Int =>
            slider.deafTo(slider)
            println(s"Index $i")
            slider.value = i
            slider.listenTo(slider)
          case _ =>
        }
      }
    }

    val p1 = new Param(
      new CheckBox,
      new Label("Foo"),
      new Slider { value = 10 },
      new Spinner(new SpinnerNumberModel(10, 0, 100, 1))
    )
    val p2 = new Param(
      new CheckBox { selected = true },
      new Label("Bar"),
      new Slider,
      new Spinner(new SpinnerNumberModel(50, 0, 100, 1))
    )
    val params = List(p1, p2)

    horizontal = Seq(
      Par(params.map(r => r.check: GroupPanel.Element): _*),
      Par(params.map(r => r.label: GroupPanel.Element): _*),
      new Par { params.foreach(r => contents += r.slider) },
      new Par { params.foreach(r => contents += r.index ) }
    )

    vertical = Seq(
      params.map { p =>
        Par(Center)(p.check, p.label, p.slider, p.index)
      }: _*
    )
  }

  private class MyPanel extends GroupPanel {
    val label     = new Label("Label:")
    val textField = new TextField(20)
  }
}
