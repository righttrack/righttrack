package models

package object results {

  private[results] trait Result extends Message

  trait RetrieveMessage[+T] {
    def item: T

    implicit def toOption: Option[T] = this match {
      case NotFound => None
      case x => Some(x.item)
    }
  }

  case class RetrieveResult[T](item: T) extends RetrieveMessage[T]

  case object NotFound extends RetrieveMessage[Nothing] {
    def item = throw new NoSuchElementException
  }


  trait CreateMessage[+E <: Exception, +T] {
    def created: Boolean
    def error: Option[E]
    def item: T
  }

  // TODO: Use more specific exception types
  case class CreateResult[T](created: Boolean, error: Option[Exception], item: T)
    extends CreateMessage[Exception, T]

}
