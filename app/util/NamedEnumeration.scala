package util

import play.api.libs.json.Format
import serializers.StandardFormat

import scala.util.{Failure, Success, Try}

sealed abstract class NamedEnumeration extends Enumeration {

  type Value <: NamedVal

  protected class NamedVal(val name: String) extends Val(name)

  def value(name: String): Value

  def findValueTry(named: String): Try[Value] = {
    findValue(named) match {
      case Some(value) => Success(value)
      case None => Failure(new IllegalArgumentException(s"No ${getClass.getName}.NamedVal with name '$named'"))
    }
  }

  def findValue(named: String): Option[Value] = {
    values.find(_.toString == named).map(_.asInstanceOf[Value])
  }

  @inline final def findValueOrElse(named: String, orElse: => Value): Value = {
    findValue(named) getOrElse orElse
  }

  @inline def findValueOrThrow(named: String): Value = findValueTry(named).get

  implicit lazy val format: Format[Value] = {
    StandardFormat.enum(this)
  }

  @transient @volatile private var valuesChecked = false
  override def values: ValueSet = {
    if (valuesChecked)
      super.values
    else {
      val values = super.values
      val broken = values collect {
        case value if !value.isInstanceOf[NamedVal] =>
          value.toString
      }
      valuesChecked = true
      if (broken.nonEmpty) {
        // this kinda sucks, but the stupid Value method is final.
        throw new Error(s"${getClass.getName} has the following values that do not extend from NamedVal: {${broken.mkString(", ")}}")
      }
      else values
    }
  }
}

class NamedValueEnum extends NamedEnumeration {
  override type Value = NamedVal

  override def value(name: String): Value = new NamedVal(name)
}

abstract class RefinedNamedEnum extends NamedEnumeration
