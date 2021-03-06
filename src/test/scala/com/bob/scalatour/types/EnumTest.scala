package com.bob.scalatour.types

import com.bob.scalatour.Light
import org.scalatest.FunSuite

object Weekday extends Enumeration {
  val Mon, Tue, Wed, Thu, Fri, Sat, Sun = Value

  def validate(weekday: Weekday.Value): Unit = {
    assert(values.contains(weekday))
  }
}

object SMonth {

  sealed trait Enum

  case object Jan extends Enum

  case object Feb extends Enum

  case object Mar extends Enum

  case object Apr extends Enum

  case object May extends Enum

  case object Jun extends Enum

  case object Jul extends Enum

  case object Aug extends Enum

  case object Sep extends Enum

  case object Oct extends Enum

  case object Nov extends Enum

  case object Dec extends Enum

  val values = Seq(Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec)

  def validate(month: Enum): Unit = {
    assert(values.contains(month))
  }
}

class EnumTest extends FunSuite {

  test("java enum") {
    assert(Light.valueOf("green") == Light.green)
    assert(Light.valueOf("yellow") == Light.yellow)
    assert(Light.valueOf("red") == Light.red)
  }

  test("scala enum") {
    Weekday.values.foreach(v => Weekday.validate(_))
  }

  test("alternative scala enum") {
    SMonth.values.foreach(SMonth.validate(_))
  }
}
