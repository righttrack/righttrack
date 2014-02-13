package services

import scala.concurrent.{ExecutionContext, Future}
import play.api.Play
import play.api.libs.ws.WS
import play.api.libs.json.{JsString, JsObject, JsValue}


trait GithubService {

//  def countAllCommits: Future[JsValue]  // not done
  def fetchGithubUserUrls: Future[JsValue]  // not done
  def fetchGithubPushEvents: Future[JsValue]  // not done
  def fetchRepoStats: Future[JsValue]  // not done
  def fetchPublicEvents: Future[JsValue]  // not done
  def getJson: Future[JsValue]  //

}

case class AccessToken(token: String) extends AnyVal

class WSGithubService extends GithubService {

  private[this] implicit val application = Play.current
  private[this] implicit val context = ExecutionContext.global

  def getJson: Future[JsValue] = WS.url(s"https://api.github.com/orgs/octokit/repos").get() map { response =>
    JsObject(Seq(
      "response" -> JsObject(Seq(
        "source" -> JsString("GitHubService"),
        "data" -> response.json
      ))
    ))
  }

  def fetchGithubPushEvents: Future[JsValue] = ???  // todo use mongo access to get data from database

  def fetchGithubUserUrls(username: String)(implicit access_token: AccessToken): Future[JsValue] = {

    WS.url(s"https://api.github.com/users/$username").get() map { response =>
      JsObject(Seq(
        "data" -> response.json
      ))
    }
  }

  def fetchPublicEvents: Future[JsValue] = ???

  override def fetchGithubUserUrls: Future[JsValue] = ???

  override def fetchRepoStats: Future[JsValue] = ???
}

