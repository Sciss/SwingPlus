package de.sciss.swingplus

import scala.swing.{SwingApplication, Dialog, Alignment, Label, TextField, Component}
import scala.swing.Swing.EmptyIcon
import javax.swing.UIManager
import java.awt.Font

object KeyValueDialogTest extends SwingApplication {
  def startup(args: Array[String]): Unit = {
    UIManager.getInstalledLookAndFeels.find(_.getName contains "GTK+").foreach { info =>
      UIManager.setLookAndFeel(info.getClassName)
    }

    // somehow the slider doesn't yield correct baseline information?
    // val value = new Slider
    val value = new TextField("Foo")
    value.font = new Font("Serif", Font.BOLD, 24)
    val res   = keyValueDialog(value)
    println(res)
  }

  def keyValueDialog(value: Component, title: String = "New Entry", defaultName: String = "Name"): Option[String] = {
    val ggName  = new TextField(10)
    ggName.text = defaultName

    val box = new GroupPanel {
      val lbName  = new Label( "Name:", EmptyIcon, Alignment.Right)
      val lbValue = new Label("Value:", EmptyIcon, Alignment.Right)
      horizontal  = Seq(Par(Trailing)(lbName, lbValue), Par          (ggName , value))
      vertical    = Seq(Par(Baseline)(lbName, ggName ), Par(Baseline)(lbValue, value))
    }

    val res = Dialog.showConfirmation(message = box.peer, optionType = Dialog.Options.OkCancel,
      messageType = Dialog.Message.Question, title = title)

    if (res == Dialog.Result.Ok) {
      val name    = ggName.text
      Some(name)
    } else {
      None
    }
  }
}
