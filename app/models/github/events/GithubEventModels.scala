package models.github.events

import models.{ModelView, EntityId, Entity}
import models.common.Email
import org.joda.time.DateTime

trait Event[T <: Entity] {
  
  val id: EventId
  
  val data: T
  
  val timestamp: DateTime
}

case class EventId(value: String) extends AnyVal with EntityId

case class GithubEvent(id: EventId, data: GithubPushEventData, timestamp: DateTime)
  extends Event[GithubPushEventData]

case class GithubPushEventData (
  repository: Repository,
  commitCount: Int,
  githubUser: GithubUser,
  pushedAt: DateTime
)
case class GithubPushEventId(value: String) extends EntityId

case class Repository(id: RepositoryId, name: String, isPrivate: Boolean, pushDatetime: DateTime) extends Entity
case class RepositoryId(value: String) extends AnyVal with EntityId

case class GithubUser(name: String, email: Email)

