package example

trait ExampleStack {

  def stackable(): Unit
}

trait StackableA extends ExampleStack {

  abstract override def stackable(): Unit = {
    println("A start")
    super.stackable()
    println("A end")
  }
}

trait StackableB extends ExampleStack {

  abstract override def stackable(): Unit = {
    println("B start")
    super.stackable()
    println("B end")
  }
}

class DumbClass extends ExampleStack {

  override def stackable(): Unit = {
    println("Derp")
  }
}

object Run extends App {

  val aThenB = new DumbClass with StackableA with StackableB
  aThenB.stackable()

  println()

  val bThenA = new DumbClass with StackableB with StackableA
  bThenA.stackable()
}
