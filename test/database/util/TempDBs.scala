package database.util

import database.mongo.util._
import org.specs2.mutable.FragmentsBuilder
import scala.concurrent.ExecutionContext

/**
 * A trait for self-typing when you need an execution context.
 */
trait HasExecutionContext {

  implicit def executionContext: ExecutionContext
}

/**
 * Mixin to provide the global execution context.
 */
trait GlobalExecutionContext extends HasExecutionContext {

  implicit def executionContext: ExecutionContext = ExecutionContext.global
}

trait HasMongoProvider {

  def mongo: TempCollectionProvider
}

/**
 * Helper trait for using the pre-configured test database providers.
 */
trait TempDBs
  extends DelayedInit
  with HasMongoProvider {
  self: FragmentsBuilder with HasExecutionContext =>

  override val mongo: TempCollectionProvider =
    new LocalhostTempCollectionProvider(CollectionNameGenerator.default)(ExecutionContext.global)

  override def delayedInit(body: => Unit): Unit = {
    body
    self.step(mongo.dropAllCollections())
  }
}
