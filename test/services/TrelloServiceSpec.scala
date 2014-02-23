package services

import org.specs2.mutable.Specification
import scala.concurrent.Await
import scala.concurrent.duration._


class TrelloServiceSpec extends Specification {

  "TrelloServiceSpec" should {

    "return the username" in {

      val service = new TrelloService()

      val ninjaButts = Await.result(service.fetchMemberData, Duration(2, SECONDS))


      ninjaButts must be_=== ("farhansyed1")

    }

  }
}