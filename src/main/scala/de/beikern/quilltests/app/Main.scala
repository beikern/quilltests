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

package de.beikern.quilltests.app

import akka.actor.ActorSystem
import akka.stream.scaladsl.{ Sink, Source }
import akka.{ Done, NotUsed }
import de.beikern.quilltests.contexts.{ AkkaContext, CassandraContext, QuillCtx }
import de.beikern.quilltests.daos.Dao.{ Bar, Foo }
import de.beikern.quilltests.evidences.SinkEvidences
import de.beikern.quilltests.typeclasses.SinkLike

import scala.concurrent.Future

object Main
    extends App
    with AkkaContextImpl
    with CassandraContextImpl
    with SinkEvidences
    with GetSink {

  val sourceFoo: Source[Foo, NotUsed] = Source.fromIterator(
      () => List(Foo("today", 1), Foo("is", 2), Foo("the_day", 3)).toIterator
  )
  sourceFoo.to(getSink).run

  val sourceBar: Source[Bar, NotUsed] = Source.fromIterator(
      () => List(Bar("today", 1), Bar("is", 2), Bar("the_day", 3)).toIterator
  )
  sourceBar.to(getSink).run

}

trait AkkaContextImpl extends AkkaContext {
  override implicit lazy val system = ActorSystem("QuillTestActorSystem")
}

trait CassandraContextImpl extends CassandraContext {
  override implicit lazy val quillCtx = new QuillCtx("ctx")
}

trait GetSink { self: AkkaContext with CassandraContext =>

  def getSink[T](implicit ev: SinkLike[T]): Sink[T, Future[Done]] = {
    ev.getSink
  }
}

// Trying to improve type classes here, no luck for now :(
/*trait GetSinkTwo[T] { self: AkkaContext with CassandraContext =>

  def getSink: Sink[T, Future[Done]]

}

object GetSinkTwo {
  def apply[T:SinkLike]: SinkLike[T] = implicitly

}*/
