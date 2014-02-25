package database.mongo

import org.specs2.mutable.Specification
import models.tasks.{TaskId, Task}
import services.impl.JavaUUIDGenerator
import scala.concurrent.Await
import scala.concurrent.duration._
import reactivemongo.api.collections.default.BSONCollection
import models.github.events.GithubPushEventData

class GithubPushCollectionITSpec extends Specification {

  import reactivemongo.api._
  import scala.concurrent.ExecutionContext.Implicits.global

  // gets an instance of the driver
  // (creates an actor system)
  val driver = new MongoDriver
  val connection = driver.connection(List("localhost"))

  // Gets a reference to the database "plugin"
  val db: DB = connection("plugin")

  val idGen = new JavaUUIDGenerator

  // Gets a reference to the collection "acoll"
  // By default, you get a BSONCollection.

  "GithubPushCollection" should {

    "save a push event" in {
      val coll = new GithubPushCollection(db[BSONCollection]("githubPushTest"))
      // mock JSON from GitHub (full POST)
      // test serializer
      //
      val pushEvent = GithubPushEventData(TaskId(idGen.next()), "test-task-1")
      val result = Await.result(coll.add(pushEvent), Duration(2, SECONDS))
      result should be_=== (true)
    }
  }
}
