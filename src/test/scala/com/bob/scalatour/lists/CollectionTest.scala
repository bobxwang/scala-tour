package com.bob.scalatour.lists

import org.scalatest.FunSuite

import scala.collection.immutable.ListMap
import scala.collection.parallel.immutable.ParRange
import scala.collection.parallel.{ParSeq, ParMap, ParSet}
import scala.collection.{SortedMap, SortedSet, mutable}

/**
 * Nil是一个空的List，定义为List[Nothing]
 */
class CollectionTest extends FunSuite {

  /**
   * fold is more generic than reduce, reduceLeft is just a convenience method, it is equivalent to
   * list.tail.foldLeft(list.head)(_)
   * 下面是两个方法签名
   * def foldLeft [B] (z: B)(f: (B, A) => B): B
   * def reduceLeft [B >: A] (f: (B, A) => B): B
   * 还有个scan/scanLeft系列，跟上面不一样的是，scan会把返回的东西给扁平化，即不是返回一个数，而是每步运算的值做为一个List的元素返回
   */


  test("basiclist") {
    def toList(v: Int) = List(v - 1, v, v + 1)
    val list = List(1, 2, 3)

    assert(list == 1 :: 2 :: 3 :: Nil)
    assert(list == List(1) ::: List(2, 3))
    assert(list == 1 :: List(2, 3))
    assert(list == 1 +: List(2, 3))
    assert(list == List(1, 2) :+ 3)
    assert(list == List(1) ++ List(2, 3))
    assert(list == List(1) ++: List(2, 3))
    assert((0 /: list)(_ + _) == 6)
    assert(6 == (list :\ 0)(_ + _))

    assert(list(2) == 3) // select by index,from zero

    assert(list == List(1, 1, 2, 2, 3, 3).distinct)
    assert(list == (List(1) union List(2, 3)))
    assert(list == (List(-2, -1, 0, 1, 2, 3) intersect List(1, 2, 3, 4, 5, 6)))

    assert(list.length == 3 && list.size == 3)
    assert(list.lengthCompare(list.size) == 0)
    assert(list.lengthCompare(list.size - 1) == 1)
    assert(list.nonEmpty)
    assert(List().isEmpty)

    assert(list.head == 1)
    assert(list.headOption.get == 1)
    assert(list.tail == List(2, 3))
    assert(list.tails.toList == List(List(1, 2, 3), List(2, 3), List(3), List()))
    assert(list.init == List(1, 2))
    assert(list.inits.toList == List(List(1, 2, 3), List(1, 2), List(1), List()))
    assert(list.last == 3)
    assert(list.lastOption.get == 3)
    assert(list.lastIndexOf(3) == 2)
    assert(list.lastIndexOfSlice(List(3)) == 2)
    assert(list.lastIndexWhere(_ > 2) == 2)

    // collect 偏函数在过滤的时候就直接输出，少去本来先filter再map的动作，即省去一次循环
    assert(list.collect { case i if i % 2 == 0 => i } == List(2))
    assert(list.collectFirst { case i if i % 2 == 0 => i } == Some(2))
    assert(list.contains(1))
    assert(list.containsSlice(List(2, 3)))
    assert(list.startsWith(List(1, 2)))
    assert(list.endsWith(List(2, 3)))
    assert(list.count(_ > 0) == 3)

    assert((List(1, 2) diff List(2, 3)) == List(1))
    assert((List(2, 3) diff List(1, 2)) == List(3))

    assert((list drop 1) == List(2, 3))
    assert(list.dropWhile(_ < 2) == List(2, 3))
    assert(list.dropRight(1) == List(1, 2))

    assert((list take 2) == List(1, 2))
    assert(list.takeWhile(_ < 3) == List(1, 2))
    assert(list.takeRight(1) == List(3))

    assert(list.min == 1)
    assert(list.minBy(_ * 2) == 1)
    assert(list.max == 3)
    assert(list.maxBy(_ * 2) == 3)
    assert(list.aggregate(0)(_ + _, _ + _) == 6)

    assert(list.filter(_ > 1) == List(2, 3))
    assert(list.filter(_ > 1).map(_ * 2) == List(4, 6))
    assert(list.filterNot(_ > 1) == List(1))
    assert(list.find(_ > 2).get == 3)

    // flatten平铺返回一列表
    assert(List(List(1), List(2), List(3)).flatten == list)
    assert(List(Some(1), None, Some(3), None).flatten == List(1, 3))

    assert(list.map(_ * 2) == List(2, 4, 6))
    assert(List("abc").map(_.toUpperCase) == List("ABC"))
    assert(list.map(i => toList(i)) == List(List(0, 1, 2), List(1, 2, 3), List(2, 3, 4)))

    assert(list.flatMap(i => List(i * 2)) == List(2, 4, 6))
    assert(List("abc").flatMap(_.toUpperCase) == List('A', 'B', 'C'))
    assert(list.flatMap(i => toList(i)) == List(0, 1, 2, 1, 2, 3, 2, 3, 4))

    assert(list.fold(0)(_ + _) == 6)
    assert(list.foldLeft(0)(_ + _) == 6)
    assert(list.foldRight(0)(_ + _) == 6)
    assert(list.foldLeft(List[Int]())((tail, head) => head :: tail) == List(3, 2, 1))
    assert(list.foldRight("4")((head, tail) => head + tail) == "1234")
    assert(list.reverse.foldLeft("4")((tail, head) => head + tail) == "1234")
    val words = List("Hello, ", "world!")
    assert(words.fold("")(_ + _) == "Hello, world!")
    assert(words.foldLeft("")(_ + _) == "Hello, world!")
    assert(words.foldRight("")(_ + _) == "Hello, world!")

    assert(List(2, 4, 6) === (for (i <- list) yield i * 2))
    assert(List(2, 4, 6) === (for (i <- list if i > 0) yield i * 2))
    assert(list.forall(_ > 0))
    list foreach { i => assert(i > 0) }

    assert(list.groupBy(_ % 2 == 0) == Map(false -> List(1, 3), true -> List(2)))
    assert(list.grouped(1).toList == List(List(1), List(2), List(3)))

    assert(list.indexOf(1) == 0)
    assert(list.indexOfSlice(List(2, 3)) == 1)
    assert(list.indexWhere(_ > 2) == 2)
    assert(list.indices.length == 3)
    for (i <- 0 to 2) assert(list.isDefinedAt(i))
    assert(list.hasDefiniteSize)

    assert("123" == list.mkString)

    assert(list.padTo(7, 0) == List(1, 2, 3, 0, 0, 0, 0))
    assert(list.patch(0, List(4, 5, 6), 3) == List(4, 5, 6))
    assert((List[Int](2), List[Int](1, 3)) == list.partition(_ % 2 == 0))
    assert(list.permutations.toList == List(List(1, 2, 3), List(1, 3, 2), List(2, 1, 3), List(2, 3, 1), List(3, 1, 2), List(3, 2, 1)))
    assert(list.prefixLength(_ > 0) == 3)
    assert(list.product == 6)

    assert(list == List.range(1, 4))
    assert(list.reduce(_ + _) == 6)
    assert(list.reduceLeft(_ + _) == 6)
    assert(list.reduceLeftOption(_ + _).get == 6)
    assert(list.reduceRight(_ + _) == 6)
    assert(list.reduceRightOption(_ + _).get == 6)
    assert(list.repr == list)
    assert(list == List(3, 2, 1).reverse)
    assert(list.reverseMap(_ * 2) == List(6, 4, 2))

    assert(list sameElements List(1, 2, 3))
    assert(list.segmentLength(_ > 0, 0) == 3)

    assert(list == List(3, 2, 1).sortBy(i => i))
    assert(list == List(3, 2, 1).sorted)
    assert(List(1, 2, 3).sortWith(_ > _) == List(3, 2, 1))
    assert(List(3, 2, 1).sortWith(_ < _) == List(1, 2, 3))

    assert(list.scan(0)(_ + _) == List(0, 1, 3, 6))
    assert(list.scanLeft(0)(_ + _) == List(0, 1, 3, 6))
    assert(list.scanRight(0)(_ + _) == List(6, 5, 3, 0))

    assert(list.slice(0, 2) == List(1, 2))
    assert(List(List(1), List(2), List(3)) == list.sliding(1).toList)
    assert((List[Int](1), List[Int](2, 3)) == list.span(_ < 2))
    assert((List[Int](1, 2), List[Int](3)) == list.splitAt(2))
    assert(list.sum == 6)

    assert(List(Set(1, 2), Set(3, 4), Set(5, 6)).transpose == List(List(1, 3, 5),
      List(2, 4, 6)))
    assert(List(1, 2, 1) == list.updated(index = 2, elem = 1))
    assert(List(2, 4, 6) == list.withFilter(_ > 0).map(_ * 2))

    assert((1 to 100).map(_ % 10).filter(_ > 5).sum == 300) // strict, slowest
    assert((1 to 100).view.map(_ % 10).filter(_ > 5).sum == 300) // non-strict, fast
    assert((1 to 100).iterator.map(_ % 10).filter(_ > 5).sum == 300) // non-strict, fastest
    assert((1 to 100).toStream.map(_ % 10).filter(_ > 5).sum == 300) // non-strict, fastest

    assert((List[Int](1, 3), List[Int](2, 4)) == List((1, 2), (3, 4)).unzip)
    assert(List((1, 3), (2, 4)) == (List(1, 2) zip List(3, 4)))
    assert(List((1, 3), (2, 4), (3, 5)) == List(1, 2, 3).zipAll(List(3, 4, 5), 0, 1))
    assert(List((1, 0), (2, 1), (3, 2)) == list.zipWithIndex)
  }

  test("basicset") {
    val set = Set(1, 2)
    assert(set == Set(1) + 2)
    assert(set == Set(1, 2, 3) - 3)
    assert(set == Set(1) ++ Set(2))
    assert(set == Set(1, 2, 3, 4) -- List(3, 4))
    assert(set == (Set(-1, 0, 1, 2) & Set(1, 2, 3, 4)))
    assert(Set(-1, 0) == (Set(-1, 0, 1, 2) &~ Set(1, 2, 3, 4)))
    assert(Set(3, 4) == (Set(1, 2, 3, 4) &~ Set(-1, 0, 1, 2)))
    assert((0 /: set)(_ + _) == 3)
    assert(3 == (set :\ 0)(_ + _))
    assert(set.size == 2 && set.contains(1) && set.contains(2))
    assert(set.empty.isEmpty)
    val a = Set(1, 2, 3, 4, 5, 6)
    val b = Set(3, 4, 7, 8, 9, 10)
    assert(a.intersect(b) == Set(3, 4))
    assert(a.union(b) == Set(5, 10, 1, 6, 9, 2, 7, 3, 8, 4))
    assert(a.diff(b) == Set(5, 1, 6, 2))
  }

  test("sorted set") {
    val set = SortedSet(3, 2, 1)
    val list = set.toIndexedSeq
    assert(list(0) == 1)
    assert(list(1) == 2)
    assert(list(2) == 3)
  }

  test("mutable set") {
    val set = mutable.Set(1, 2)
    assert((set += 3) == Set(1, 2, 3))
    assert((set -= 3) == Set(1, 2))
    assert((set -= 2) == Set(1))
    assert((set -= 1) == Set())
    assert((set ++= List(1, 2)) == Set(1, 2))
    assert((set --= List(1, 2)) == Set())
  }

  test("basic map") {
    val map = Map(1 -> 1, 2 -> 2)
    assert(map(1) == 1)
    assert(map.get(2).get == 2)
    assert(map.getOrElse(3, -1) == -1)
    assert(map.contains(1))
    assert(map == Map(1 -> 1) + (2 -> 2))
    assert(map == Map(1 -> 1, 2 -> 2, 3 -> 3) - 3)
    assert(map == Map(1 -> 1) ++ Map(2 -> 2))
    assert(map == Map(1 -> 1, 2 -> 2, 3 -> 3, 4 -> 4) -- List(3, 4))
    assert((0 /: map.keys)(_ + _) == 3)
    assert(3 == (map.keys :\ 0)(_ + _))
    assert((0 /: map.values)(_ + _) == 3)
    assert(3 == (map.values :\ 0)(_ + _))
    assert(map.size == 2 && map(1) == 1 && map(2) == 2)
    assert(map.keySet == Set(1, 2) && map.values.toSet == Set(1, 2))
    assert(map.empty.isEmpty)
  }

  test("list map") {
    val map = ListMap(3 -> 3, 2 -> 2, 1 -> 1)
    val list = map.keys.toIndexedSeq
    assert(list(0) == 3)
    assert(list(1) == 2)
    assert(list(2) == 1)
  }

  test("sorted map") {
    val map = SortedMap(3 -> 3, 2 -> 2, 1 -> 1)
    val list = map.keys.toIndexedSeq
    assert(list(0) == 1)
    assert(list(1) == 2)
    assert(list(2) == 3)
  }

  test("mutable map") {
    val map = mutable.Map(1 -> 1, 2 -> 2)
    assert((map += 3 -> 3) == Map(1 -> 1, 2 -> 2, 3 -> 3))
    assert((map -= 3) == Map(1 -> 1, 2 -> 2))
    assert((map -= 2) == Map(1 -> 1))
    assert((map -= 1) == Map())
    assert((map ++= List(1 -> 1, 2 -> 2)) == Map(1 -> 1, 2 -> 2))
    assert((map --= List(1, 2)) == Map())
  }

  test("vector") {
    val vector = Vector(1, 2)
    assert(vector.length == 2 && vector(0) == 1 && vector(1) == 2)
    assert(vector.reverse === Vector(2, 1))
    assert(vector === 1 +: Vector(2))
    assert(vector === Vector(1) :+ 2)
    assert(vector === Vector(1) ++ Vector(2))
    assert(vector === Vector(1) ++: Vector(2))
    assert((0 /: vector)(_ + _) == 3)
    assert(3 == (vector :\ 0)(_ + _))
  }

  test("array") {
    val array = Array(1, 2)
    assert(array.length == 2 && array(0) == 1 && array(1) == 2)
    assert(array.reverse === Array(2, 1))
    assert(array === 1 +: Array(2))
    assert(array === Array(1) :+ 2)
    assert(array === Array(1) ++ Array(2))
    assert(array === Array(1) ++: Array(2))
    assert((0 /: array)(_ + _) == 3)
    assert(3 == (array :\ 0)(_ + _))
  }

  test("stream") {
    val numberOfEvens = (1 to 100).toStream.count(_ % 2 == 0)
    assert(numberOfEvens == 50)
  }

  test("tuple") {
    val cityStateZip = ("placida", "florida", 33946)
    assert(cityStateZip._1 == "placida" && cityStateZip._2 == "florida" && cityStateZip._3 == 33946)
    val (first, last, age) = ("fred", "flintstone", 99)
    assert(first == "fred" && last == "flintstone" && age == 99)
  }

  test("fifo queue") {
    val queue = mutable.Queue(1, 2)
    queue enqueue 3
    assert(3 == queue.last)
    assert(queue.dequeue() == 1)
    assert(queue.dequeue() == 2)
    assert(queue.dequeue() == 3)
    assert(queue.isEmpty)
  }

  test("lifo stack") {
    val stack = mutable.Stack(2, 1)
    stack push 3
    assert(3 == stack.pop)
    assert(2 == stack.pop)
    assert(1 == stack.pop)
    assert(stack.isEmpty)
  }

  test("array buffer") {
    val buffer = mutable.ArrayBuffer(1, 2)
    assert((buffer += 3) == mutable.ArrayBuffer(1, 2, 3))
    assert((buffer -= 3) == mutable.ArrayBuffer(1, 2))
    assert((buffer -= 2) == mutable.ArrayBuffer(1))
    assert((buffer -= 1) == mutable.ArrayBuffer())
  }

  test("list buffer") {
    val buffer = mutable.ListBuffer(1, 2)
    assert((buffer += 3) == mutable.ListBuffer(1, 2, 3))
    assert((buffer -= 3) == mutable.ListBuffer(1, 2))
    assert((buffer -= 2) == mutable.ListBuffer(1))
    assert((buffer -= 1) == mutable.ListBuffer())
  }

  test("string builder") {
    val builder = new StringBuilder
    builder.append("a")
    builder.append("b")
    builder.append("c")
    assert(builder.toString() == "abc")
    assert(builder.result() == "abc")
    assert(builder.reverse.result() == "cba")
  }

  test("range") {
    assert((1 until 10) == Range(1, 10, 1))
    assert((10 until 1 by -1) == Range(10, 1, -1))
    assert((1 to 10) == Range.inclusive(1, 10, 1))
  }

  test("par set") {
    val set = ParSet(1 to 1000000: _*)
    assert(set.sum == 1784293664)
  }

  test("par map") {
    val m = for (i <- 1 to 1000000) yield (i, i)
    val map = ParMap(m: _*)
    assert(map.values.sum == 1784293664)
  }

  test("par seq") {
    val seq = ParSeq(1 to 1000000: _*)
    assert(seq.sum == 1784293664)
  }

  test("par range") {
    val range = ParRange(1, 1000000, 1, inclusive = true)
    assert(range.sum == 1784293664)
  }

  test("as java, as scala") {
    import scala.collection.JavaConverters._
    val list = List(1, 2, 3).asJava
    assert(list.size == 3)
    assert(list.asScala.sum == 6)
  }
}