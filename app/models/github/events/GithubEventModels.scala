package models.github.events

import models.{EntityId, Entity}
import models.common.{EventId, Event, Email}
import org.joda.time.DateTime
import models.meta.{EntityTypes, EntityType}


case class GithubPushEvent(id: EventId, data: GithubPushEventData, timestamp: DateTime)
  extends Event[GithubPushEventData]

case class GithubPushEventData (
  repository: Repository,
  commitCount: Int,
  githubUser: GithubUser,
  pushedAt: DateTime
)

case class GithubUser(name: String, email: Email)

case class Repository(id: RepositoryId, name: String, isPrivate: Boolean, pushDatetime: DateTime) extends Entity
case class RepositoryId(value: String) extends AnyVal with EntityId {
  def entityType: EntityType = EntityTypes.Repository
}

