package database.mongo

import database.dao.GithubPushEventDAO
import scala.concurrent.Future
import database.FindResult
import models.github.events._
import play.api.libs.json.Json
import models.github.events.GithubPushEventData
import models.users.UserId
import play.modules.reactivemongo.json.collection.JSONCollection
import models.github.events.GithubSerializers._
import models.common.EventId
import scala.concurrent.duration.Duration


class GithubPushCollection(collection: JSONCollection)
  extends BaseCollection
  with GithubPushEventDAO {

  override def retrievePreviousWeek(
    user: UserId,
    repoId: Int,
    from: Duration
  ): Future[FindResult[GithubPushEventData]] = ???

  override def add(pushEvent: GithubPushEvent): Future[Boolean] = {
    val json = Json.toJson(pushEvent)
    collection.insert(json) map (_.ok)
  }

  override def findById(id: EventId): Future[Option[GithubPushEvent]] = {
    collection.find(
      Json.obj(
        "id" -> id.value
      )
    ).one[GithubPushEvent]
  }
}





