import reflect._
import scala.reflect.runtime.{ currentMirror => cm }
import scala.reflect.runtime.universe._

// case class instance with default args

// Persons entering this site must be 18 or older, so assume that
case class Person(name: String, age: Int = 18) {
  require(age >= 18)
}

object Test extends App {

  // Person may have some default args, or not.
  // normally, must Person(name = "Guy")
  // we will Person(null, 18)
  def newCase[A]()(implicit t: ClassTag[A]): A = {
    val claas = cm classSymbol t.runtimeClass
    val modul = claas.companionSymbol.asModule
    val im = cm reflect (cm reflectModule modul).instance
    defaut[A](im, "apply")
  }

  def defaut[A](im: InstanceMirror, name: String): A = {
    val at = newTermName(name)
    val ts = im.symbol.typeSignature
    val method = (ts member at).asMethod

    // either defarg or default val for type of p
    def valueFor(p: Symbol, i: Int): Any = {
      val defarg = ts member newTermName(s"$name$$default$$${i+1}")
      if (defarg != NoSymbol) {
        println(s"default $defarg")
        (im reflectMethod defarg.asMethod)()
      } else {
        println(s"def val for $p")
        p.typeSignature match {
          case t if t == typeOf[String] => null
          case t if t == typeOf[Int]    => 0
          case x                        => throw new IllegalArgumentException(x.toString)
        }
      }
    }
    val args = (for (ps <- method.paramss; p <- ps) yield p).zipWithIndex map (p => valueFor(p._1,p._2))
    (im reflectMethod method)(args: _*).asInstanceOf[A]
  }

  assert(Person(name = null) == newCase[Person]())
}