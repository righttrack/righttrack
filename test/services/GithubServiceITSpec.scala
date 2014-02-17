package services

import org.specs2.mutable.Specification
import play.api.Play.configuration
import play.api.test._
import play.api.test.Helpers._
import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.Play

case class AccessToken(token: String) extends AnyVal

class GithubServiceITSpec extends Specification {

  val config = Play.configuration(Play.current)
  implicit val access_token = config.getString("github.oauth.token").asInstanceOf[String with AccessToken]
  implicit val other_thing = "oaiwjdoiawjdoi"

  "Github integration" should {

    "allow fetching all public events for user" in {
      running(FakeApplication()) {
        val githubService = new WSGitHubService
        val events = githubService.fetchPublicEvents("jeffmay")
        val awaited = Await.result(events, Duration("10 seconds"))
        println(awaited)
        pending
      }
    }

  }

}
