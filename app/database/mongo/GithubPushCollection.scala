package database.mongo

import database.dao.GithubPushEventDAO
import models.common.EventId
import models.github.events.GithubPushEventData
import models.github.events.GithubSerializers._
import models.github.events._
import models.users.UserId
import play.api.libs.json.Json
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Future
import scala.concurrent.duration.Duration


class GithubPushCollection(collection: JSONCollection)
  extends BaseCollection
  with GithubPushEventDAO {

  override def retrievePreviousWeek(
    user: UserId,
    repoId: Int,
    from: Duration
  ): Finds[GithubPushEventData] = ???

  override def add(pushEvent: GithubPushEvent): Future[Boolean] = {
    val json = Json.toJson(pushEvent)
    collection.insert(json) map (_.ok)
  }

  override def findById(id: EventId): FindsOne[GithubPushEvent] = {
    collection.find(
      Json.obj(
        "id" -> id.value
      )
    ).one[GithubPushEvent]
  }
}





