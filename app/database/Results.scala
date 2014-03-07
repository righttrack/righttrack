package database

import models.{EntityId, Entity}
import scala.util.{Success, Failure, Try}
import scala.reflect._
import scala.reflect.runtime.universe._
import scala.util.control.NonFatal

/**
 * Monadic abstraction for different types of database results.
 *
 * Results are designed to be abstract from the library that builds them.
 * Providing an intermediate type allows implicit conversion and helpful
 * builders that define a DSL for combining results from various data sources.
 */
sealed trait Result[+A] {
  self =>

  def get: A

  def isEmpty: Boolean

  def isError: Boolean

  @inline final def nonEmpty: Boolean = !isEmpty

  @inline final def getOrElse[B >: A](otherwise: => B): B = {
    if (isEmpty) get
    else otherwise
  }

  @inline final def toOption: Option[A] = if (isEmpty) None else Some(get)

  def toTry: Try[A]

}

/**
 * The result of querying a data source.
 *
 * The query can return a FindResult[A] or FindOneResult[A] to indicate that only one should
 * be returned. A FindResult
 */
// TODO: In order for this to become useful, we have to support futures and enumeratees
sealed trait FindResult[A] extends Result[Seq[A]] {
  self =>

  protected def tagged: TypeTag[A]

  /** Returns this $result if it is nonempty '''and''' applying the predicate $p to
    * this $result's value returns true. Otherwise, return $none.
    *
    *  @param  p   the predicate used for testing.
    */
  @inline final def filter(p: A => Boolean): FindResult[A] =
    if (isEmpty) this
    else FindResult(this.get.filter(p))(tagged)

  /** Apply the given procedure $f to the result's value,
    *  if it is nonempty. Otherwise, do nothing.
    *
    *  @param  f   the procedure to apply.
    *  @see map
    *  @see flatMap
    */
  @inline final def foreach[U](f: A => U): Unit =
    if (!isEmpty) this.get foreach f

  /** Returns a $some containing the result of applying $f to this $result's
    * value if this $result is nonempty.
    * Otherwise return $notfound.
    *
    *  @note This is similar to `flatMap` except here,
    *  $f does not need to wrap its result in an $option.
    *
    *  @param  f   the function to apply
    *  @see flatMap
    *  @see foreach
    */
  @inline final def map[B : TypeTag](f: A => B): FindResult[B] =
    if (isEmpty) NotFound[B] else Found(this.get map f)

  /** Returns the result of applying $f to this $result's values if
    * this $result is nonempty.
    * Returns $notfound if this $result is empty.
    * Slightly different from `map` in that $f is expected to
    * return an $findresult (which could be $notfound or $founderror).
    *
    *  @param  f   the function to apply
    *  @see map
    *  @see foreach
    */
  @inline final def flatMap[B : TypeTag](f: A => FindResult[B]): FindResult[B] =
    this match {
      case error: FoundError[A] => FoundError[B](
        s"Stopped after previous failure to find instances of ${error.exception.tagged.tpe}",
        error.exception
      )
      case _ =>
        val iter = this.get.iterator
        var results: Seq[B] = Seq.empty
        while (iter.hasNext) {
          val item = iter.next()
          f(item) match {
            case Found(items) =>
              results ++= items
            case cant =>
              return cant
          }
        }
        Found(results)
    }

  class WithFilter(p: A => Boolean) {
    def map[B : TypeTag](f: A => B): FindResult[B] = self.filter(p).map(f)
    def flatMap[B : TypeTag](f: A => FindResult[B]): FindResult[B] = self filter p flatMap f
    def foreach[U](f: A => U): Unit = self filter p foreach f
    def withFilter(q: A => Boolean): WithFilter = new WithFilter(x => p(x) && q(x))
  }
}

object FindResult {

  def apply[A : TypeTag](block: => TraversableOnce[A]): FindResult[A] = {
    try {
      val items = block
      if (items.isEmpty) NotFound[A]
      else Found(items)
    }
    catch {
      case NonFatal(exception) => FoundError(s"Encountered ${exception.getLocalizedMessage}", exception)
    }
  }
}

sealed trait FindOneResult[A] extends FindResult[A]

object FindOneResult {

  def apply[A : TypeTag](block: => A): FindOneResult[A] = {
    try FoundOne(block)
    catch {
      case NonFatal(exception) => FoundError(s"Encountered ${exception.getLocalizedMessage}", exception)
    }
  }
}

class FoundError[A](val exception: NotFoundException[A])
  extends FindOneResult[A] {

  override protected def tagged: TypeTag[A] = exception.tagged

  override def isError: Boolean = true

  override def isEmpty: Boolean = true

  override def get: Seq[A] = throw exception

  @inline final override def toTry: Try[Seq[A]] = Failure(exception)
}

object FoundError {

  def apply[E](why: String, cause: Throwable = null)(implicit tag: TypeTag[E]): FoundError[E] =
    new FoundError[E](new NotFoundException(tag, why, cause))

  def unapply[E](result: FindResult[E]): Option[Throwable] = result match {
    case has: FoundError[E] => Some(has.exception)
    case _ => None
  }
}

case class NotFoundException[E](tagged: TypeTag[E], why: String, cause: Throwable = null)
  extends DatabaseException(s"Could not find ${tagged.tpe}: $why", cause)

class Found[A](val entities: Seq[A], tag: TypeTag[A])
  extends FindResult[A] {

  override protected def tagged: TypeTag[A] = tag

  override def isError: Boolean = false

  override def isEmpty: Boolean = false

  override def get: Seq[A] = entities

  @inline final override def toTry: Try[Seq[A]] = Success(entities)
}

class NotFound[T](tag: TypeTag[T]) extends Found[T](Seq.empty, tag) {

  override def isError: Boolean = false

  override def isEmpty: Boolean = true
}

object NotFound {

  def apply[T](implicit tag: TypeTag[T]): NotFound[T] = new NotFound(tag)
}

//case object NotFound extends Found[Nothing](Seq.empty, TypeTag.Nothing) {
//
//  override def isError: Boolean = false
//
//  override def isEmpty: Boolean = true
//}

/**
 * Extracts and builds FindResult with zero or more items.
 */
object Found {

  def apply[A : TypeTag](items: TraversableOnce[A]): FindResult[A] = {
    val stream = items.toStream
    if (stream.isEmpty) NotFound[A]
    else if (stream.tail.isEmpty) new FoundOne(stream.head, typeTag[A])
    else new Found(stream, typeTag[A])
  }

  def unapply[A](result: FindResult[A]): Option[Seq[A]] = result.toOption
}

/**
 * FoundResult that contains only one item.
 */
class FoundOne[A](value: A, tag: TypeTag[A])
  extends Found(Seq(value), tag)
  with FindOneResult[A] {

  override protected def tagged: TypeTag[A] = tag
}

object FoundOne {

  def apply[A](value: A)(implicit tag: TypeTag[A]): FoundOne[A] = new FoundOne(value, tag)

  def unapply[A](result: FindResult[A]): Option[A] =
    if (result.isEmpty || result.get.isEmpty || result.get.tail.nonEmpty) None
    else result.get.headOption
}

/**
 * Result of creating an Entity.
 */
sealed trait CreateResult[E <: Entity] extends Result[E] {

  def created: Boolean

  def getId: EntityId = get.id
}

class EntityNotCreated[E <: Entity : ClassTag](why: String, cause: Throwable = null)
  extends DatabaseException(s"Could not create ${classTag[E].runtimeClass.getCanonicalName}: $why", cause)

class CreatedError[E <: Entity] private[CreatedError] (val exception: Throwable)
  extends CreateResult[E] {

  override def created: Boolean = false

  override def isEmpty: Boolean = false

  override def isError: Boolean = true

  override def get: Nothing = throw exception

  @inline final override def toTry: Try[Nothing] = Failure(exception)
}

object CreatedError {

  def apply[E <: Entity : ClassTag](why: String, cause: Throwable = null): CreatedError[E] =
    new CreatedError[E](new EntityNotCreated[E](why, cause))

  def unapply[E <: Entity](result: CreateResult[E]): Option[Throwable] = {
    result match {
      case has: CreatedError[E] => Some(has.exception)
      case _ => None
    }
  }
}

case class Created[T <: Entity](entity: T)
  extends CreateResult[T] {

  override def created: Boolean = true

  override def isEmpty: Boolean = true

  override def isError: Boolean = false

  override def get: T = entity

  @inline final override def toTry: Try[T] = Success(entity)
}
