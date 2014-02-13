package services

import scala.concurrent.{ExecutionContext, Future}
import akka.actor.Actor
import play.api.Play
import play.api.libs.ws.WS
import play.api.libs.json.{JsString, JsObject, JsValue}
import scala.xml.NodeSeq

trait GitHubService {

  def getXml: Future[NodeSeq]

  def getJson: Future[JsValue]

}

class GitHubServiceActor extends GitHubService with Actor {

  private[this] implicit val application = Play.current
  private[this] implicit val context = ExecutionContext.global

  def getXml = WS.url("https://api.github.com/orgs/octokit/repos").get() map { response =>
    <response>
      <source>GitHubServiceActor</source>
      <data>
        ${response.body}
      </data>
    </response>
  }

  def getJson = WS.url("https://api.github.com/orgs/octokit/repos").get() map { response =>
    JsObject(Seq(
      "response" -> JsObject(Seq(
        "source" -> JsString("GitHubServiceActor"),
        "data" -> response.json
      ))
    ))
  }

  def receive = {
    case GitHubServiceActor.GetXml => getXml.map(sender !)
    case GitHubServiceActor.GetJson => getJson.map(sender !)
  }
}

object GitHubServiceActor {
  case object GetXml
  case object GetJson
}



class WSGitHubService extends GitHubService {

  private[this] implicit val application = Play.current
  private[this] implicit val context = ExecutionContext.global

  def getXml = WS.url("https://api.github.com/orgs/octokit/repos").get() map { response =>
    <response>
      <source>WSGitHubService</source>
      <data>
        ${response.body}
      </data>
    </response>
  }

  def getJson = WS.url("https://api.github.com/orgs/octokit/repos").get() map { response =>
    JsObject(Seq(
      "response" -> JsObject(Seq(
        "source" -> JsString("GitHubServiceActor"),
        "data" -> response.json
      ))
    ))
  }
}

