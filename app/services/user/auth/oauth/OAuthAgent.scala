package services.user.auth.oauth

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{ActorLogging, ActorRef, Actor}
import cake.{DefaultIdGen, GlobalExecutionContext}
import models.auth._
import models.users.UserId
import scala.concurrent.duration._
import scala.util.Random
import services.user.auth.AuthService
import services.user.auth.AuthService.RedirectTo
import util.RandomHelpers

class OAuthAgent
  extends Actor
  with ActorLogging
  with DefaultIdGen
  with GlobalExecutionContext {

  // TODO: Move to better location

  val accessUrl = "https://github.com/login/oauth/authorize"
  val clientId = "f4acdd617ec14c77e73d"
  val clientSecret = "b6dc4317e08c9a772bb3cbfbef1b2aaba9b35743"
  val callbackUrl = "http://localhost:9000/#/authorize/github/oauth"
  val scopes = Set("user")

  val registrationTimeout = Duration(5, SECONDS)
  
  override def receive: Actor.Receive = {

    case OAuthAgent.WaitForToken(caller, userId, state) =>
      caller ! RedirectTo(
        s"$accessUrl?client_id=$clientId&client_secret=$clientSecret&scope=${ scopes.mkString(",") }&state=$state"
      )
      context.become(waiting(sender, userId, state))
      context.system.scheduler.scheduleOnce(registrationTimeout) {
        log.debug("Killing agent")
        self ! Stop
      }
  }

  def waiting(service: ActorRef, expectedUserId: UserId, expectedState: String): Actor.Receive = {

    case OAuthAgent.TokenReceived(userId, token, state) if userId == expectedUserId && state == expectedState =>
      val account = OAuthAccount(idGen next AuthAccountId, token)
      service ! AuthService.AccountAuthorized(userId, account)
      log.debug("Killing agent")
      self ! Stop

    case OAuthAgent.TokenReceived(userId, token, state) =>
      log.warning(s"Unexpected state received: '$state'. Ignoring potential man-in-the-middle attack.")
  }
}

object OAuthAgent extends RandomHelpers {

  case class WaitForToken(caller: ActorRef, userId: UserId, state: String = Random.randomString(24))

  case class TokenReceived(userId: UserId, token: OAuthToken, state: String)

}
