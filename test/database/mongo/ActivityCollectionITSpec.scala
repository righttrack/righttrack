package database.mongo

import org.specs2.mutable.Specification
import models.activity.verb.Creates._
import services.impl.JavaUUIDGenerator
import scala.concurrent.Await
import scala.concurrent.duration._
import reactivemongo.api._
import models.tasks.TaskId
import models.users.UserId
import models.activity.ActivityId
import models.activity.Activity
import play.modules.reactivemongo.json.collection.JSONCollection
import org.joda.time.DateTime

class ActivityCollectionITSpec extends Specification {

  import scala.concurrent.ExecutionContext.Implicits.global

  // gets an instance of the driver
  // (creates an actor system)
  val driver = new MongoDriver
  val connection = driver.connection(List("localhost"))
  val db: DB = connection("test_righttrack")
  val idGen = new JavaUUIDGenerator

  "ActivityCollection" should {

    "save a task" in {
      val coll = new ActivityCollection(db[JSONCollection]("test_activity_save"))
      val activity = Activity(ActivityId(idGen.next()), UserId("1"), creates, TaskId("1"), new DateTime)
      val result = Await.result(coll.record(activity), Duration(5, SECONDS))
      result.get should beEqualTo(activity)
    }
  }
}
