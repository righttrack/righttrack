package database.mongo

import cake.{DefaultIdGen, GlobalExecutionContext}
import database.util.TempDBs
import models.activity.verb.Creates._
import models.activity.{Activity, ActivityId}
import models.tasks.TaskId
import models.users.UserId
import org.joda.time.DateTime
import org.specs2.mutable.Specification

import scala.concurrent.Await
import scala.concurrent.duration._

class ActivityCollectionITSpec
  extends Specification
  with TempDBs
  with DefaultIdGen
  with GlobalExecutionContext {

  val activities = new ActivityCollection(new BaseJsonCollection(mongo.tempJSONCollection("activities")))

  "ActivityCollection" can {

    "save a task" in {
      val activity = Activity(idGen next ActivityId, UserId("1"), creates, TaskId("1"), new DateTime)
      val result = Await.result(activities.record(activity), Duration(5, SECONDS))
      result.getOrThrow should beEqualTo(activity)
    }
  }
}
