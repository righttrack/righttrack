package controllers.api

import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Akka
import services.{WSGitHubService, GitHubService, GitHubServiceActor}
import akka.actor.{TypedProps, TypedActor, Props}
import akka.pattern.ask
import scala.concurrent.duration._
import play.api.Play
import akka.util.Timeout
import scala.concurrent.ExecutionContext
import play.api.libs.json.JsValue
import scala.xml.NodeSeq

class GitHubRest extends Controller {

  private implicit val application = Play.current
  private implicit val context = ExecutionContext.global
  private implicit val timeout = Timeout(2.seconds)

  private val githubActor = Akka.system.actorOf(Props[GitHubServiceActor])
  private val typedGithubActor: GitHubService = TypedActor(Akka.system).typedActorOf(TypedProps[WSGitHubService]())
  private val wsGithub = new WSGitHubService

  def getJsonViaActor = Action {
    Async {
      (githubActor ? GitHubServiceActor.GetJson).mapTo[JsValue].map(Ok(_)) recover {
        case ex => ServiceUnavailable(ex.getLocalizedMessage)
      }
    }
  }

  def getXmlViaActor = Action {
    Async {
      (githubActor ? GitHubServiceActor.GetXml).mapTo[NodeSeq].map(Ok(_)) recover {
        case ex => ServiceUnavailable(ex.getLocalizedMessage)
      }
    }
  }

  def getJsonViaTypedActor = Action {
    Async {
      typedGithubActor.getJson.map(Ok(_)) recover {
        case ex => ServiceUnavailable(ex.getLocalizedMessage)
      }
    }
  }

  def getXmlViaTypedActor = Action {
    Async {
      typedGithubActor.getXml.map(Ok(_)) recover {
        case ex => ServiceUnavailable(ex.getLocalizedMessage)
      }
    }
  }

  def getJsonViaWSService = Action {
    Async {
      wsGithub.getJson.map(Ok(_)) recover {
        case ex => ServiceUnavailable(ex.getLocalizedMessage)
      }
    }
  }

  def getXmlViaWSService = Action {
    Async {
      wsGithub.getXml.map(Ok(_)) recover {
        case ex => ServiceUnavailable(ex.getLocalizedMessage)
      }
    }
  }

}
