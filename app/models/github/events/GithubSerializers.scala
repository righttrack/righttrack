package models.github.events

import play.api.libs.json._
import org.joda.time.DateTime
import models.common.{EventId, CommonSerializers, Email}
import models.ReadsId
object GithubSerializers extends CommonSerializers {

  implicit lazy val githubPushEventDataWriter: Format[GithubPushEventData] = Json.format[GithubPushEventData]
  implicit lazy val githubUserWriter: Format[GithubUser] = Json.format[GithubUser]
  implicit lazy val repositoryWriter: Format[Repository] = Json.format[Repository]
  implicit lazy val githubPushEventWriter: Format[GithubPushEvent] = Json.format[GithubPushEvent]

  implicit lazy val repositoryIdReader: Reads[RepositoryId] = new Reads[RepositoryId] {
    override def reads(json: JsValue): JsResult[RepositoryId] = json match {
      case JsString(id) => JsSuccess(RepositoryId(id))
      case _ => JsError("RepositoryId's id value must be a JsString")
    }
  }

  implicit lazy val eventIdReader: Reads[EventId] = new Reads[EventId] {
    override def reads(json: JsValue): JsResult[EventId] = json match {
      case JsString(id) => JsSuccess(EventId(id))
      case _ => JsError("EventId's id value must be a JsString")
    }
  }


  object Db {


  }

  object Raw {  // reading off the wire

    implicit val pushEventReader = new Reads[GithubPushEventData] {
      override def reads(json: JsValue): JsResult[GithubPushEventData] = json match {
        case JsObject(fields) =>
          val foundCommitsCount: Option[Int] = fields.collectFirst({
            case ("commits", JsArray(commits)) => commits.size
            // what if this is none?
          })

          val foundGithubUser: Option[GithubUser] = fields collectFirst {
            case ("pusher", JsObject(pusherFields)) => pusherFields
          } flatMap { pusherFields =>

            var foundName: Option[String] = None
            var foundEmail: Option[String] = None

            for (field <- pusherFields) field match {
              case ("name", JsString(value)) =>
                foundName = Some(value)
              case ("email", JsString(value)) =>
                foundEmail = Some(value)
              case _ =>
            }
            val user: Option[GithubUser] = for {
              name <- foundName
              email <- foundEmail
            } yield {
              GithubUser(name, Email(email))
            }
            user
          }

          var foundPushedAt: Option[DateTime] = None

          val foundRepository: Option[Repository] = fields collectFirst {
            case ("repository", JsObject(repositoryFields)) => repositoryFields
          } flatMap { repositoryFields =>

            var foundId: Option[String] = None
            var foundName: Option[String] = None
            var foundIsPrivate: Option[Boolean] = None
            var foundPushedAt: Option[DateTime] = None

            for (field <- repositoryFields) field match {
              case ("id", JsNumber(value)) =>
                foundId = Some(value.toString())
              case ("name", JsString(value)) =>
                foundName = Some(value)
              case ("private", JsBoolean(value)) =>
                foundIsPrivate = Some(value)
              case ("pushed_at", JsNumber(value)) =>
                foundPushedAt = Some(DateTime.parse(value.toString()))
              case _ =>
            }
            val repo: Option[Repository] = for {
              id <- foundId
              name <- foundName
              pushedAt <- foundPushedAt
              isPrivate <- foundIsPrivate
            } yield {
              Repository(RepositoryId(id), name, isPrivate, pushedAt)
            }
            repo
          }
          val data: Option[GithubPushEventData] = for {
            commits <- foundCommitsCount
            pushedAt <- foundPushedAt
            user <- foundGithubUser
            repository <- foundRepository
          } yield {
            GithubPushEventData(repository, commits, user, pushedAt)
          }

          data match {
            case Some(event) => JsSuccess(event)
            case _ => JsError("Could not serialize GithubPushEventData")

          }

      }

    }

  }


}
