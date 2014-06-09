package database.mongo

import cake.{DefaultIdGen, GlobalExecutionContext}
import database.mongo.util.DefaultTimeouts
import database.util.TempDBs
import models.common.{Email, EventId}
import models.github.events._
import _root_.util.DateTimeHelpers
import org.specs2.mutable.Specification

import scala.concurrent.Await

class GithubPushCollectionITSpec
  extends Specification
  with TempDBs
  with DefaultIdGen
  with DefaultTimeouts
  with GlobalExecutionContext {

  val testCollection = new GithubPushCollection(mongo.tempJSONCollection("githubPushEvents"))

  "GithubPushCollection" should {

    "save a GithubPushEvent to the database" in {
      val when = DateTimeHelpers.now
      val repo = Repository(RepositoryId("12345"), "SaveTest", true, when)
      val pushEventData = GithubPushEventData(repo, 1, GithubUser("Saver", Email("igot@saved.com")), when)
      val pushEvent = GithubPushEvent(idGen next EventId, pushEventData, when)
      val result = Await.result(testCollection.add(pushEvent), queryTimeout)
      result should be_===(true)
    }

    "allow save and retrieval of a GithubPushEvent" in {
      val when = DateTimeHelpers.now
      val repo = Repository(RepositoryId("891011"), "SaveRetrieveTest", true, when)
      val pushEventData = GithubPushEventData(
        repo,
        1,
        GithubUser("SaveRetriever", Email("igot@savedandretrieved.com")),
        when
      )
      val eventId = idGen next EventId
      val pushEvent = GithubPushEvent(eventId, pushEventData, when)
      val result = Await.result(testCollection.add(pushEvent), queryTimeout)
      result should be_===(true)

      val retrieved = Await.result(testCollection.findById(eventId), queryTimeout)
      val data = retrieved.get.data
      data.pushedAt.getZone should be equalTo pushEvent.data.pushedAt.getZone
      data.pushedAt should be equalTo pushEvent.data.pushedAt
      retrieved should be equalTo Some(pushEvent)
    }
  }
}
