package controllers.api

import services.{AccessToken, WSGithubService}
import scala.concurrent.duration._
import play.api.Play
import akka.util.Timeout
import scala.concurrent.{Future, ExecutionContext}
import play.api.mvc.{SimpleResult, Action, Controller}
import play.api.libs.json.{JsError, JsSuccess, Json}
import models.github.events.{GithubPushEvent, GithubSerializers, GithubPushEventData}
import database.dao.GithubPushEventDAO
import com.google.inject.{Singleton, Inject}
import services.impl.JavaUUIDGenerator
import models.common.{Email, EventId}
import GithubSerializers.Raw._
import org.joda.time.DateTime

@Singleton
class GithubRest @Inject()(dao: GithubPushEventDAO)
  extends Controller {

  private implicit val application = Play.current
  private implicit val context = ExecutionContext.global
  private implicit val timeout = Timeout(2.seconds)

  // todo remove hardcoding here
  private implicit def token: AccessToken = AccessToken("2bf804343e534478e7a98b4512e83f87df10dcb2")

  private val wsGithub = new WSGithubService
  private val idGen = new JavaUUIDGenerator

  def fetchGithubPushEvents = Action.async {
    wsGithub.fetchPublicEvents.map(Ok(_)) recover {
      case ex => ServiceUnavailable(ex.getLocalizedMessage)
    }
  }

  def handleGithubPushEvents = Action.async {
    request =>
      request.body.asJson match {
        case Some(json) =>
          val data = Json.fromJson[GithubPushEventData](json)
          val tellMe: Future[SimpleResult] = data match {
            case JsSuccess(pushEvent, _) =>
              val event = GithubPushEvent(EventId(idGen.next()), pushEvent, new DateTime)
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
