/*
 * Copyright (c) 2016 JLCM
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.beikern.quilltests.graphstages

import akka.stream.stage._
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import com.datastax.driver.core.Session

class CassandraBackpressure[A](session: Session) extends GraphStage[FlowShape[A, A]] {

  private def queriesInFlightPerHost(session: Session): Int = {
    import scala.collection.JavaConversions._

    val iterable: Iterable[Int] = for {
      host <- session.getState.getConnectedHosts
    } yield { session.getState.getInFlightQueries(host) }
    iterable.toList.sum
  }

  val in  = Inlet[A]("CassandraBackpressure.in")
  val out = Outlet[A]("CassandraBackpressure.out")

  override val shape: FlowShape[A, A] = FlowShape.of(in, out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = {
    new TimerGraphStageLogic(shape) {
      // All mutable state must be inside the graphStageLogic
      var open = true

      setHandler(in, new InHandler {
        override def onPush: Unit = {
          push(out, grab(in))
        }
      })

      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          import scala.concurrent.duration._

          if (queriesInFlightPerHost(session) < 900 && open) {
            pull(in)
          } else {
            open = false
            scheduleOnce(None, 3.seconds)
          }
        }
      })

      override protected def onTimer(timerKey: Any): Unit = {
        println("open = true!")
        open = true
        pull(in)
      }

    }
  }

}
