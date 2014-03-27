package database.mongo

import cake.GlobalExecutionContext
import database.mongo.util.DefaultTimeouts
import database.util.TempDBs
import models.common.{EventId, Email}
import models.github.events._
import org.joda.time.DateTime
import org.specs2.mutable.Specification
import scala.concurrent.Await
import services.impl.JavaUUIDGenerator

class GithubPushCollectionITSpec
  extends Specification
  with TempDBs
  with DefaultTimeouts
  with GlobalExecutionContext {

  val idGen = new JavaUUIDGenerator

  val testCollection = new GithubPushCollection(mongo.tempJSONCollection("githubPushEvents"))

  "GithubPushCollection" should {

    "save a GithubPushEvent to the database" in {

      val repo = Repository(RepositoryId("12345"), "SaveTest", true, new DateTime)
      val pushEventData = GithubPushEventData(repo, 1, GithubUser("Saver", Email("igot@saved.com")), new DateTime)
      val pushEvent = GithubPushEvent(EventId(idGen.next()), pushEventData, new DateTime)
      val result = Await.result(testCollection.add(pushEvent), queryTimeout)
      result should be_===(true)
    }

    "allow save and retrieval of a GithubPushEvent" in {

      val repo = Repository(RepositoryId("891011"), "SaveRetrieveTest", true, new DateTime)
      val pushEventData = GithubPushEventData(
        repo,
        1,
        GithubUser("SaveRetriever", Email("igot@savedandretrieved.com")),
        new DateTime
      )
      val eventId = EventId(idGen.next())
      val pushEvent = GithubPushEvent(eventId, pushEventData, new DateTime)
      val result = Await.result(testCollection.add(pushEvent), queryTimeout)
      result should be_===(true)

      val retrieved = Await.result(testCollection.findById(eventId), queryTimeout)
      retrieved should be equalTo Some(pushEvent)
    }
  }
}
