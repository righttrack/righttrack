package services.user.auth.oauth

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{ActorLogging, ActorRef, Actor}
import cake.{GlobalExecutionContext, HasInjector}
import models.auth._
import models.users.UserId
import scala.concurrent.duration._
import scala.util.Random
import services.user.auth.AuthService.RedirectTo
import services.user.auth.{AuthParams, AuthService}
import util.RandomHelpers

class OAuthAgent
  extends Actor
  with ActorLogging
  with GlobalExecutionContext
  with RandomHelpers {

  // TODO: Move to better location

  val accessUrl = "https://github.com/login/oauth/authorize"
  val clientId = "f4acdd617ec14c77e73d"
  val clientSecret = "b6dc4317e08c9a772bb3cbfbef1b2aaba9b35743"
  val callbackUrl = "http://localhost:9000/#/authorize/github/oauth"
  val scopes = Set("user")

  val registrationTimeout = Duration(5, SECONDS)
  
  override def receive: Actor.Receive = {

    case OAuthAgent.WaitForToken(caller, userId) =>
      val state = Random.randomString(24)
      caller ! RedirectTo(
        s"$accessUrl?client_id=$clientId&client_secret=$clientSecret&scope=${ scopes.mkString(",") }&state=$state"
      )
      context.become(waiting(sender, state))
      context.system.scheduler.scheduleOnce(registrationTimeout) {
        self ! Stop
      }
  }

  def waiting(service: ActorRef, expecting: String): Actor.Receive = {

    case params @ OAuthAgent.OAuthParams(_, _, state) =>
      if (state == expecting) {
        service ! AuthService.AccountAuthorized(params)
        self ! Stop
      }
      else {
        log.warning(s"Unexpected state received: '$state'. Ignoring potential man-in-the-middle attack.")
      }
  }
}

object OAuthAgent {

  case class WaitForToken(caller: ActorRef, userId: UserId)

  case class OAuthParams(userId: UserId, account: OAuthAccount, state: String) extends AuthParams {
    override final type Account = OAuthAccount
  }

}
