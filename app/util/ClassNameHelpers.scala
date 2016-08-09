package util

import scala.reflect.runtime.universe

object ClassNameHelpers {

  def typeNameOf[T](implicit tag: universe.TypeTag[T]): String = tag.tpe.toString

  def typeName(cls: Class[_]): String = universe.rootMirror.classSymbol(cls).toType.toString
}
