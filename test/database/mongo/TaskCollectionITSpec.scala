package database.mongo

import models.tasks.{TaskId, Task}
import org.specs2.mutable.Specification
import scala.concurrent.Await
import scala.concurrent.duration._
import services.impl.JavaUUIDGenerator
import database.util.{GlobalExecutionContext, TempDBs}

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
      val task = Task(TaskId(idGen.next()), "test-task-1")
      val result = Await.result(coll.add(task), Duration(2, SECONDS))
      result should be_=== (true)
    }
  }
}
