package controllers.api

import akka.util.Timeout
import cake.DefaultIdGen
import com.google.inject.{Inject, Singleton}
import database.dao.GithubPushEventDAO
import models.common.EventId
import models.github.events.{GithubPushEvent, GithubPushEventData}
import org.joda.time.DateTime
import play.api.Play
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, Controller, Result}
import services.{AccessToken, WSGithubService}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GithubController @Inject()(dao: GithubPushEventDAO)
  extends BaseController
  with DefaultIdGen {

  private implicit val application = Play.current
  private implicit val context = ExecutionContext.global
  private implicit val timeout = Timeout(2.seconds)

  // todo remove hardcoding here
  private implicit def token: AccessToken = AccessToken("2bf804343e534478e7a98b4512e83f87df10dcb2")

  private val wsGithub = new WSGithubService

  def fetchGithubPushEvents = Action.async {
    wsGithub.fetchPublicEvents.map(Ok(_)) recover {
      case ex => ServiceUnavailable(ex.getLocalizedMessage)
    }
  }

  def handleGithubPushEvents = Action.async {
    request =>
      request.body.asJson match {
        case Some(json) =>
          val data = Json.fromExternalJson(GithubPushEventData, json)
          val tellMe: Future[Result] = data match {
            case JsSuccess(pushEvent, _) =>
              val event = GithubPushEvent(idGen next EventId, pushEvent, new DateTime)
              dao.add(event) map {
                success =>
                  if (success) Ok
                  else InternalServerError
              }
            case JsError(ex) => Future(BadRequest)
          }
          tellMe
        case _ => Future(BadRequest("Bad request; not JSON"))
      }
  }

  def getJsonViaWSService = Action.async {
    wsGithub.fetchPublicEvents.map(Ok(_)) recover {
      case ex => ServiceUnavailable(ex.getLocalizedMessage)
    }
  }
}
