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

import akka.Done
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}
import com.datastax.driver.core.Cluster.Builder
import com.datastax.driver.core.{Cluster, HostDistance, PoolingOptions}
import de.beikern.quilltests.contexts.{AkkaContext, CassandraContext, QuillCtx}
import de.beikern.quilltests.daos.Dao.{Bar, Foo}
import de.beikern.quilltests.evidences.SinkEvidences
import de.beikern.quilltests.typeclasses.SinkLike

import scala.concurrent.Future
import scala.util.Random

object Main extends App with AkkaContextImpl with CassandraContextImpl with SinkEvidences with GetSink {

  val sourceFoo = Source.fromIterator(
      () =>
        Iterator.continually(
            Foo(
                Random.nextString(10000),
                Random.nextInt(Integer.MAX_VALUE)
            )
      )
  )

  sourceFoo.to(getSink).run

  val sourceBar = Source.fromIterator(
      () =>
        Iterator.continually(
            Bar(
                Random.nextString(10000),
                Random.nextInt(Integer.MAX_VALUE)
            )
      )
  )
  sourceBar.to(getSink).run

}

trait AkkaContextImpl extends AkkaContext {
  override implicit lazy val system = ActorSystem("QuillTestActorSystem")
}

trait CassandraContextImpl extends CassandraContext {

  val clusterBuilder: Builder = Cluster.builder()
  clusterBuilder.addContactPoints("127.0.0.1")
  clusterBuilder.withPoolingOptions(
      new PoolingOptions()
        .setMaxRequestsPerConnection(HostDistance.LOCAL, 1024)
        .setMaxRequestsPerConnection(HostDistance.REMOTE, 256)
        .setMaxQueueSize(1024)
//        .setConnectionsPerHost(HostDistance.LOCAL, 4, 10)
//        .setConnectionsPerHost(HostDistance.REMOTE, 2, 4)
  )

  val cluster: Cluster = clusterBuilder.build()

  override implicit lazy val quillCtx = new QuillCtx(cluster, "quill_test", 1000)

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
