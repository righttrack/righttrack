package models.github.events

import models.{EntityId, Entity}

case class GithubPushEvent(id: GithubPushEventId, description: String) extends Entity
case class GithubPushEventId(value: String) extends EntityId

