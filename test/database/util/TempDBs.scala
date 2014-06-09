package database.util

import database.mongo.util._
import org.specs2.matcher.ConcurrentExecutionContext
import org.specs2.mutable.FragmentsBuilder
import org.specs2.specification.After

trait HasMongoProvider {

  def mongo: TempCollectionProvider
}

/**
 * Helper trait for using the pre-configured test database providers.
 */
trait TempDBs
  extends After
  with HasMongoProvider {
  self: FragmentsBuilder with ConcurrentExecutionContext =>

  override val mongo: TempCollectionProvider =
    new LocalhostTempCollectionProvider(CollectionNameGenerator.default)

  override def after: Any = {
    self.step(mongo.dropAllCollections())
  }
}
