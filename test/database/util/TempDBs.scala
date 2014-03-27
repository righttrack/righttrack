package database.util

import cake.HasExecutionContext
import database.mongo.util._
import org.specs2.mutable.FragmentsBuilder
import scala.concurrent.ExecutionContext

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
