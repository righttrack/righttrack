package serializers.external

import models.github.events.GithubPushEventData
import models.github.events.GithubUser
import models.github.events.Repository
import models.github.events.RepositoryId
import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._
import serializers.{CommonSerializers, InternalEntityIdFormat}

object GithubSerializers extends CommonSerializers with InternalEntityIdFormat {

  implicit val userReader: Reads[GithubUser] = Json.reads[GithubUser]

  implicit val repositoryReader: Reads[Repository] = {
    val read =
      (__ \ "id").read[Long].map(iid => RepositoryId(iid.toString)) and
      (__ \ "name").read[String] and
      (__ \ "private").read[Boolean] and
      (__ \ "pushed_at").read[String].map(millis => new DateTime(millis))
    read((id, name, isPrivate, pushedAt) => Repository(id, name, isPrivate, pushedAt))
  }

  implicit val pushEventReader: Reads[GithubPushEventData] = {
    val read =
      (__ \ "commits").read[Int] and
      (__ \ "pusher").read[GithubUser] and
      (__ \ "repository").read[Repository]
    read((commits, user, repo) => GithubPushEventData(repo, commits, user, repo.pushDatetime))
  }

}