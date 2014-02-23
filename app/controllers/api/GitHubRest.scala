package controllers.api

import services.{AccessToken, WSGitHubService}
import scala.concurrent.duration._
import play.api.Play
import akka.util.Timeout
import scala.concurrent.ExecutionContext
import play.api.mvc.{Action, Controller}


class GitHubRest extends Controller {

  private implicit val application = Play.current
  private implicit val context = ExecutionContext.global
  private implicit val timeout = Timeout(2.seconds)
  // todo remove hardcoding here
  private implicit def token: AccessToken = AccessToken("2bf804343e534478e7a98b4512e83f87df10dcb2") // todo remove

  private val wsGithub = new WSGitHubService
  // todo work on this class to map it to the service calls
  def fetchGithubPushEvents = Action {
    Async {
      wsGithub.fetchPublicEvents.map(Ok(_)) recover {
        case ex => ServiceUnavailable(ex.getLocalizedMessage)
      }
    }
  }

  def listenForGithubPushEvents = Action {
    Async {
      wsGithub.fetchPublicEvents.map(Ok(_)) recover {
        case ex => ServiceUnavailable(ex.getLocalizedMessage)
      }
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
