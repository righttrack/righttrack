package database.mongo

import database.dao.GithubPushEventDAO
import reactivemongo.api.collections.default.BSONCollection
import models.github.events.GithubPushEventData
import scala.concurrent.Future
import org.joda.time.DateTime
import models.users.UserId
import database.{Found, FindResult}
import reactivemongo.bson._
import models.users.UserId
import models.github.events.GithubPushEventData
import reactivemongo.bson.BSONInteger
import reactivemongo.bson.BSONString
import reactivemongo.api.collections.default.BSONCollection


class GithubPushCollection(collection: BSONCollection)
  extends BaseCollection
  with GithubPushEventDAO {
  override def retrievePreviousWeek(startDate: DateTime, endDate: DateTime, user: UserId, repoId: Int): Future[FindResult[GithubPushEventData]] = {
    collection.insert()
  }

  override def add(pushEvent: GithubPushEventData): Future[Boolean] = ???

  retrievePreviousWeek(???, ???, ???, ???) map {
    case Found(events) =>  // todo do something with your events
  }
}

object BSONGithubPushEvent {

  def writer: BSONDocumentWriter[GithubPushEventData] = Implicits.githubPushEventWriter

  object Implicits {

    implicit val githubPushEventWriter = new BSONDocumentWriter[GithubPushEventData] {

      override def write(pushEvent: GithubPushEventData): BSONDocument = BSONDocument(Seq(
        "repositoryId" -> BSONString(pushEvent.repository.id.value),
        "commitCount" -> BSONInteger(pushEvent.commitCount),
        "repositoryPrivate" -> BSONBoolean(pushEvent.repository.isPrivate),
        "pushedAt" -> BSONDateTime(pushEvent.pushedAt.getMillis),
        "githubUser" -> BSONString(pushEvent.githubUser.name),   // pusher.name
        "repositoryName" -> BSONString(pushEvent.repository.name)
      ))
    }
  }

}



