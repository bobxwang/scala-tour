package com.bob.scalatour.sys

import org.scalatest.FunSuite

import scala.sys.SystemProperties
import scala.sys.process.Process

class SysPropertiesTest extends FunSuite {

  test("read system properties") {
    val properties = new SystemProperties
    assert(properties.contains("java.runtime.name"))
    properties += ("objekt" -> "werks")
    assert(properties.contains("objekt"))
    properties -= "objekt"
    assert(properties.getOrElse("objekt", "empty") == "empty")
  }

  test("using shell command") {
    val file = Process("ls").lineStream.find(f => f == "build.sbt")
    assert(file.getOrElse("empty") == "build.sbt")
    val line = Process("find build.sbt").lineStream.headOption
    assert(line.getOrElse("empty") == "build.sbt")
    val lines = Process("cat .gitignore").lineStream.mkString.split("\\W+").groupBy(l => l).mapValues(l => l.length)
    assert(lines.values.max == 3)
  }
}