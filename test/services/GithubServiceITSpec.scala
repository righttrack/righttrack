import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.Play
import play.api.libs.json.JsString
import services.{WSGithubService, AccessToken}

class GithubServiceITSpec extends Specification {

  val app = FakeApplication()
  val config = Play.configuration(app)
  implicit val access_token = AccessToken(config.getString("github.oauth.token") getOrElse {
    throw new IllegalStateException("No github.oauth.token")
  })

  "GitHub integration" should {

    "allow fetching all GitHub urls for user" in {

      running(app) {

        val githubService = new WSGithubService
        val events = githubService.fetchGithubUserUrls("anxiousmodernman")(access_token)
        val awaited = Await.result(events, Duration("10 seconds"))
        println(awaited)
        awaited \ "data" \ "login" should beEqualTo(JsString("anxiousmodernman"))
      }
    }

//    "allow counting commits for all repos" in {
//
//      running(app) {
//
//        val githubService = new WSGitHubService
//        val commits = githubService.countAllCommits  //(access_token)
//        val awaited = Await.result(commits, Duration("10 seconds"))
//        println(commits)
//
//        pending
//      }
//    }
  }

}
