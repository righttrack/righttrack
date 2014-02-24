package models.github.events

import play.api.libs.json._
import org.joda.time.DateTime
import models.common.Email


object GithubSerializers {

  object Raw {

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
            case _ => JsError("Could not parse a GithubPushEventData")
          }

      }

    }

  }



}
