package serializers.api

import models.common.EventId
import models.github.events._
import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._
import serializers._

trait GithubIdSerializers extends EntityIdSerializers with StringEntityIdFormat {

  implicit lazy val eventIdReader: Reads[EventId] = Reads id EventId

  implicit lazy val repositoryIdReader: Reads[RepositoryId] = Reads id RepositoryId

}

object GithubSerializers extends Serializers
  with CommonSerializers
  with GithubIdSerializers
{

  implicit lazy val githubPushEventDataFormat: Format[GithubPushEventData] = Json.format[GithubPushEventData]
  implicit lazy val githubUserWriter: Format[GithubUser] = Json.format[GithubUser]
  implicit lazy val repositoryWriter: Format[Repository] = Json.format[Repository]

  implicit lazy val githubPushEventFormat: Format[GithubPushEvent] = Json.format[GithubPushEvent]

  object Raw {  // reading off the wire

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

}
