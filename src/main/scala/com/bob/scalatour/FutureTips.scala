package com.bob.scalatour

import java.util.concurrent.Executors

import scala.util.control.NonFatal

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * 一个Future实例总是和一个(也只能是一个)Promise实例关联在一起，
  * 如果你在REPL里调用future方法会发现返回的也是Promise
  * Future持有一个尚未完成运算结果的promise(一个简单的容器，占位符)，包含三个状态(pending,failed,completed)
  */
object FutureTips {

  case class Friend(name: String, phone: String)

  def main(args: Array[String]): Unit = {

    val ab: List[Future[Int]] = List(Future[Int](1), Future[Int] {
      4
    }, Future[Int] {
      5
    })
    val abc: Future[List[Int]] = Future.sequence(ab)
    abc.map(x => x.map(y => println(y)))
    val futureList: Future[List[Int]] = Future.traverse((1 to 100).toList)(x => Future(x * 2 - 1))
    futureList.foreach(x => println(x.sum))

    // Future.sequences可以将一个List[Future[Int]]转成一个Future[List[Int]]
    // 而Future.traverse需要两个入参，一个是T[A]，还有一个是A=>Future[B]，返回一个Future[T[B]]

    val f: Future[List[Friend]] = Future {

      (1 to 10).map(x => Friend(s"name${x}", s"phone${x}")).toList
    }

    f.onSuccess {
      case friends => println(friends.mkString(","))
    }

    f.onFailure {
      case t => println(s"An error occured,${t.getMessage}")
    }

    // reslut同步阻塞获取结果，或者超时，或者抛出异常
    // ready等待结果完成，不返回
    // import scala.concurrent.duration._
    // Await.ready(f, 0 nanos)

    // 对于其它没有像Future实现Awaitable trait的代码，可通过如下代码实现阻塞
    blocking {
      /**
        * u code
        */
    }

    println("main thread invoke done")

    // 我们也可以通过Promise的方式来创建一个Future，Future可以看作一个只读的，值还没计算的占位符。
    // 而Promise可以看作一个可写的，单次指派的容器，可以完成一个future。
    // Promise是单次指派的，你不能调用success或failure两次，否则会抛出IllegalStateException。
    // val p = Promise[Friend]
  }

  def aCompletePromiseUsingSuccess(num: Int): Future[Int] = {
    val promise = Promise[Int]()
    //    promise.success(num)
    promise.complete(Success(num))
    promise.future
  }

  val somePool = Executors.newFixedThreadPool(2)

  def sumOfThreeNumbersSequentialMap(): Future[Int] = {
    Future {
      Thread.sleep(1000)
      1
    }.flatMap(x => {
      Future {
        Thread.sleep(1000)
        2
      }.flatMap(y => {
        Future {
          Thread.sleep(1000)
          3
        }.map(z => x + y + z)
      })
    })
  }

  def someExternalDelayedCalculation(f: () => Int): Future[Int] = {
    val promise = Promise[Int]()
    val thisIsWhereWcCallSomeExternalComputation = new Runnable {
      override def run(): Unit = {
        promise.complete {
          try (Success(f()))
          catch {
            case NonFatal(msg) => Failure(msg)
          }
        }
      }
    }
    somePool.execute(thisIsWhereWcCallSomeExternalComputation)
    promise.future
  }

  def usingComplete(f: Future[List[Friend]]) = {
    f.onComplete {
      case Success(friends) => friends.foreach(println)
      case Failure(t) => println("An error has occured: " + t.getMessage)
    }
  }
}