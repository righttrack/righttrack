package database.mongo

import cake.GlobalExecutionContext
import database.util.TempDBs
import models.JavaUUIDGenerator
import models.tasks.{TaskId, Task}
import org.specs2.mutable.Specification
import scala.concurrent.Await
import scala.concurrent.duration._

class TaskCollectionITSpec
  extends Specification
  with TempDBs
  with GlobalExecutionContext {

  val idGen = new JavaUUIDGenerator

  // Gets a reference to the collection "acoll"
  // By default, you get a BSONCollection.

  "TaskCollection" should {

    "save a task" in {
      val coll = new TaskCollection(mongo.tempBSONCollection("tasks"))
      val task = Task(idGen next TaskId, "test-task-1")
      val result = Await.result(coll.add(task), Duration(2, SECONDS))
      result should be_=== (true)
    }
  }
}
