package database.dao

import org.specs2.mutable.Specification
import models.activity.verb.Creates._
import models.Version._
import database.mongo.ActivityCollection
import services.impl.JavaUUIDGenerator
import scala.concurrent.Await
import scala.concurrent.duration._
import reactivemongo.api._
import models.tasks.TaskId
import models.users.UserId
import models.activity.ActivityId
import reactivemongo.api.collections.default.BSONCollection
import models.activity.Activity

class ActivityDAOSpec extends Specification {

  import scala.concurrent.ExecutionContext.Implicits.global

  // gets an instance of the driver
  // (creates an actor system)
  val driver = new MongoDriver
  val connection = driver.connection(List("localhost"))
  val db: DB = connection("test_righttrack")
  val idGen = new JavaUUIDGenerator

  "ActivityCollection" should {

    "save a task" in {
      val coll = new ActivityCollection(db[BSONCollection]("test_activity_save"))
      val activity = Activity(ActivityId(idGen.next()), UserId("1"), creates, TaskId("1"), v1)
      val result = Await.result(coll.record(activity), Duration(2, SECONDS))
      result.get should beEqualTo(activity)
    }
  }
}
