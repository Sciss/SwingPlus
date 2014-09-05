// why is this shit package private in Scala-Swing ?!

package de.sciss.swingplus

import scala.swing.{Reactions, Publisher}

/** A publisher that subscribes itself to an underlying event source not before the first
  * reaction is installed. Can unsubscribe itself when the last reaction is uninstalled.
  */
trait LazyPublisher extends Publisher {
  import Reactions.Reaction

  protected def onFirstSubscribe (): Unit
  protected def onLastUnsubscribe(): Unit

  override def subscribe(listener: Reaction): Unit = {
    if(listeners.size == 1) onFirstSubscribe()
    // another frickin private method
    // super.subscribe(listener)
    listeners += listener
  }

  override def unsubscribe(listener: Reaction): Unit = {
    // another frickin private method
    // super.unsubscribe(listener)
    listeners -= listener
    if(listeners.size == 1) onLastUnsubscribe()
  }
}
