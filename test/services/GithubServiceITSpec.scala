package services

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import scala.concurrent.Await
import scala.concurrent.duration._

class GithubServiceITSpec extends Specification {

  //  val app = FakeApplication(new File("/home/coleman/Code/righttrack/conf/application.conf"))


//
//  val config = Play.configuration(app)
//  implicit val access_token = AccessToken(config.getString("github.oauth.token") getOrElse {
//    throw new IllegalStateException("No github.oauth.token")
//  })

  "Github integration" should {

    "allow fetching all public events for user" in {
      val app = FakeApplication()
      running(app) {

        val githubService = new WSGitHubService
        val events = githubService.fetchPublicEvents("anxiousmodernman")(AccessToken("2bf804343e534478e7a98b4512e83f87df10dcb2"))
        val awaited = Await.result(events, Duration("10 seconds"))
        println(awaited)

        pending
      }
    }
  }

}
