package controllers.api

import play.api.mvc.{Action, Controller}
import services.WSGitHubService
import scala.concurrent.duration._
import play.api.Play
import akka.util.Timeout
import scala.concurrent.ExecutionContext

class GitHubRest extends Controller {

  private implicit val application = Play.current
  private implicit val context = ExecutionContext.global
  private implicit val timeout = Timeout(2.seconds)

  private val wsGithub = new WSGitHubService



  def getJsonViaWSService = Action {
    Async {
      wsGithub.getJson.map(Ok(_)) recover {
        case ex => ServiceUnavailable(ex.getLocalizedMessage)
      }
    }
  }

}
