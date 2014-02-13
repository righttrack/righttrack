package controllers.api

import services.{AccessToken, WSGithubService}
import scala.concurrent.duration._
import play.api.Play
import akka.util.Timeout
import scala.concurrent.ExecutionContext
import play.api.mvc.{Action, Controller}
import play.api.libs.json.{JsError, JsSuccess, Json}
import models.github.events.{GithubPushEvent, GithubSerializers, GithubPushEventData}
import database.dao.GithubPushEventDAO
import com.google.inject.{Singleton, Inject}
import services.impl.JavaUUIDGenerator
import models.common.{Email, EventId}
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


  def fetchGithubPushEvents = Action {
    Async {
      wsGithub.fetchPublicEvents.map(Ok(_)) recover {
        case ex => ServiceUnavailable(ex.getLocalizedMessage)
      }
    }
  }

  def handleGithubPushEvents = Action { request =>
    request.body.asJson match {
      case Some(json) =>
        // save collection to the database
        // return either success or failure
        println(json)
        val data = Json.fromJson[GithubPushEventData](json)(GithubSerializers.Raw.pushEventReader)


        val tellMe = data match {
          case JsSuccess(pushEvent, _) =>
            Async {
              val event = GithubPushEvent(EventId(idGen.next()), pushEvent, new DateTime)
              dao.add(event) map { success =>
                if (success) Ok
                else InternalServerError
              }
            }
          case JsError(ex) => BadRequest

        }


        tellMe
      case _ => BadRequest("Bad request; not JSON")
    }
  }

  def getJsonViaWSService = Action {
    Async {
      wsGithub.fetchPublicEvents.map(Ok(_)) recover {
        case ex => ServiceUnavailable(ex.getLocalizedMessage)
      }
    }
  }

  def retrieveAccessToken: AccessToken = ???
}
