/*
 *  CloseOperation.scala
 *  (SwingPlus)
 *
 *  Copyright (c) 2013-2014 Hanns Holger Rutz. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v3+
 *
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

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
