package serializers.generic

import models.common.EventId
import models.github.events._
import play.api.libs.json._
import serializers._

trait GithubIdSerializers extends Serializers {
  self: EntityIdFormat =>

  implicit lazy val eventIdFormat: Format[EventId] = Format id EventId

  implicit lazy val repositoryIdFormat: Format[RepositoryId] = Format id RepositoryId

}

trait GithubSerializers extends CommonSerializers with GithubIdSerializers {
  self: EntityIdFormat =>

  implicit lazy val githubPushEventDataFormat: Format[GithubPushEventData] = Json.format[GithubPushEventData]

  implicit lazy val githubUserFormat: Format[GithubUser] = Json.format[GithubUser]

  implicit lazy val repositoryFormat: Format[Repository] = Json.format[Repository]

  implicit lazy val githubPushEventFormat: Format[GithubPushEvent] = Json.format[GithubPushEvent]

}