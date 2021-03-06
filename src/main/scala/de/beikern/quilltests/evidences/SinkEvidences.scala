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

package de.beikern.quilltests.evidences

import akka.Done
import akka.stream.scaladsl.Sink
import de.beikern.quilltests.contexts.{AkkaContext, CassandraContext, QuillCtx}
import de.beikern.quilltests.daos.Dao.{Bar, Foo}
import de.beikern.quilltests.typeclasses.SinkLike

import scala.concurrent.Future

trait SinkEvidences { self: AkkaContext with CassandraContext =>

  val errorLog: PartialFunction[Throwable, Unit] = PartialFunction[Throwable, Unit](
      ex => println(s"There was an error persisting elements. Stacktrace: ${ex.getMessage}")
  )
  implicit object FooSinkLike extends SinkLike[Foo] {
    override def getSink(
        implicit quillCtx: QuillCtx
    ): Sink[Foo, Future[Done]] = {
      import quillCtx._
      Sink.foreach[Foo](elem => {
        quillCtx.run(mappedFoo.insert(lift(elem))).onFailure(errorLog)
      })
    }
  }
  implicit object BarSinkLike extends SinkLike[Bar] {
    override def getSink(
        implicit quillCtx: QuillCtx
    ): Sink[Bar, Future[Done]] = {
      import quillCtx._
      Sink.foreach[Bar](elem => {
        run(mappedBar.insert(lift(elem))).onFailure(errorLog)
      })
    }
  }
}
