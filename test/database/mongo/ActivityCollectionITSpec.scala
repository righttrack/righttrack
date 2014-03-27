package database.mongo

import cake.GlobalExecutionContext
import database.util.TempDBs
import models.activity.Activity
import models.activity.ActivityId
import models.activity.verb.Creates._
import models.tasks.TaskId
import models.users.UserId
import org.joda.time.DateTime
import org.specs2.mutable.Specification
import scala.concurrent.Await
import scala.concurrent.duration._
import services.impl.JavaUUIDGenerator

class ActivityCollectionITSpec
  extends Specification
  with TempDBs
  with GlobalExecutionContext {

  val idGen = new JavaUUIDGenerator
  val activities = new ActivityCollection(mongo.tempJSONCollection("activities"))

  "ActivityCollection" can {

    "save a task" in {
      val activity = Activity(ActivityId(idGen.next()), UserId("1"), creates, TaskId("1"), new DateTime)
      val result = Await.result(activities.record(activity), Duration(5, SECONDS))
      result.getOrThrow should beEqualTo(activity)
    }
  }
}
