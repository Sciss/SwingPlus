package de.sciss.swingplus

import javax.swing.WindowConstants
import scala.annotation.switch

object CloseOperation {
  def apply(id: Int): CloseOperation = (id: @switch) match {
    case Ignore .id => Ignore
    case Exit   .id => Exit
    case Hide   .id => Hide
    case Dispose.id => Dispose
  }

  case object Ignore  extends CloseOperation { final val id = WindowConstants.DO_NOTHING_ON_CLOSE  }
  case object Exit    extends CloseOperation { final val id = WindowConstants.EXIT_ON_CLOSE        }
  case object Hide    extends CloseOperation { final val id = WindowConstants.HIDE_ON_CLOSE        }
  case object Dispose extends CloseOperation { final val id = WindowConstants.DISPOSE_ON_CLOSE     }
}
sealed trait CloseOperation { def id: Int }
