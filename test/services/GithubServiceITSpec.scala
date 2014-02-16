package services

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import scala.concurrent.Await
import scala.concurrent.duration._


class GithubServiceITSpec extends Specification {

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
