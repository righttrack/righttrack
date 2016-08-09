package models.github.events

import models.{EntityId, Entity}
import models.common.{EventId, Event, Email}
import org.joda.time.DateTime
import models.meta.{EntityTypes, EntityType}
import play.api.libs.json.{__, Reads, Json, Format}
import play.api.libs.functional.syntax._
import serializers.{IdSerializer, ExternalReader}


case class GithubPushEvent(id: EventId, data: GithubPushEventData, created: DateTime)
  extends Event[GithubPushEventData]

object GithubPushEvent extends ExternalReader[GithubPushEvent] {

  override val external: Reads[GithubPushEvent] = Json.reads[GithubPushEvent]

  implicit val internal: Format[GithubPushEvent] = Json.format[GithubPushEvent]
}

case class GithubPushEventData (
  repository: Repository,
  commitCount: Int,
  githubUser: GithubUser,
  pushedAt: DateTime
)

object GithubPushEventData extends ExternalReader[GithubPushEventData] {

  override val external: Reads[GithubPushEventData] = {
    val read =
      (__ \ "commits").read[Int] and
      (__ \ "pusher").read[GithubUser] and
      (__ \ "repository").read[Repository]
    read((commits, user, repo) => GithubPushEventData(repo, commits, user, repo.pushDatetime))
  }

  implicit val internal: Format[GithubPushEventData] = Json.format[GithubPushEventData]
}

case class GithubUser(name: String, email: Email)

object GithubUser extends ExternalReader[GithubUser] {

  override val external: Reads[GithubUser] = Json.reads[GithubUser]

  implicit val internal: Format[GithubUser] = Json.format[GithubUser]
}

case class Repository(id: RepositoryId, name: String, isPrivate: Boolean, pushDatetime: DateTime) extends Entity

object Repository extends ExternalReader[Repository] {

  override val external: Reads[Repository] = {
    val read =
      (__ \ "id").read[Long].map(id => RepositoryId(id.toString)) and
      (__ \ "name").read[String] and
      (__ \ "private").read[Boolean] and
      (__ \ "pushed_at").read[String].map(millis => new DateTime(millis))
    read((id, name, isPrivate, pushedAt) => Repository(id, name, isPrivate, pushedAt))
  }

  implicit lazy val internal: Format[Repository] = Json.format[Repository]
}

case class RepositoryId(value: String) extends AnyVal with EntityId {
  def entityType: EntityType = EntityTypes.Repository
}

object RepositoryId extends IdSerializer[RepositoryId]
