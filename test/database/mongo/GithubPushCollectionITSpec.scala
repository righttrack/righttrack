package database.mongo

import org.specs2.mutable.Specification
import services.impl.JavaUUIDGenerator
import scala.concurrent.Await
import scala.concurrent.duration._
import models.github.events._
import org.joda.time.DateTime
import models.common.{EventId, Email}
import play.modules.reactivemongo.json.collection.JSONCollection
import models.github.events.GithubPushEventData
import models.github.events.GithubUser
import play.modules.reactivemongo.json.collection.JSONCollection
import models.github.events.RepositoryId
import models.github.events.Repository


class GithubPushCollectionITSpec extends Specification {

  import reactivemongo.api._
  import scala.concurrent.ExecutionContext.Implicits.global

  // gets an instance of the driver
  // (creates an actor system)
  val driver = new MongoDriver
  val connection = driver.connection(List("localhost"))

  // Gets a reference to the database ""
  val db: DB = connection("righttrackTest")
  val idGen = new JavaUUIDGenerator

  val testCollection = new GithubPushCollection(db[JSONCollection]("githubPushTest"))


  "GithubPushCollection" should {

    "save a GithubPushEvent to the database" in {

      val repo = Repository(RepositoryId("12345"), "SaveTest", true, new DateTime)
      val pushEventData = GithubPushEventData(repo, 1, GithubUser("Saver", Email("igot@saved.com")), new DateTime)
      val pushEvent = GithubPushEvent(EventId(idGen.next()), pushEventData, new DateTime)
      val result = Await.result(testCollection.add(pushEvent), Duration(5, SECONDS))
      result should be_=== (true)
    }

    "allow save and retrieval of a GithubPushEvent" in {
      val repo = Repository(RepositoryId("891011"), "SaveRetrieveTest", true, new DateTime)
      val pushEventData = GithubPushEventData(repo, 1, GithubUser("SaveRetriever", Email("igot@savedandretrieved.com")), new DateTime)
      val eventId = EventId(idGen.next())
      val pushEvent = GithubPushEvent(eventId, pushEventData, new DateTime)
      val result = Await.result(testCollection.add(pushEvent), Duration(5, SECONDS))
      result should be_=== (true)

      val retrieved = Await.result(testCollection.findById(eventId), Duration(5, SECONDS))
      retrieved should be equalTo Some(pushEvent)

    }
  }
}
