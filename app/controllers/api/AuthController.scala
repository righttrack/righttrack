package controllers.api

import play.api.mvc.{Action, Controller}
import cake.{GlobalExecutionContext, GlobalActorSystem}
import akka.actor.Props
import services.user.auth.AuthService
import akka.pattern.ask
import models.users.UserId
import services.user.auth.AuthService.{RedirectTo, AuthStep}
import models.auth.OAuthAccount

class AuthController
  extends Controller
  with GlobalActorSystem
  with GlobalExecutionContext
  with DefaultTimeouts {

  private[this] val authService = actorSystem.actorOf(Props[AuthService])

  def authorize(id: String) = Action.async {
    (authService ? AuthService.Authorize(UserId(id), classOf[OAuthAccount])).mapTo[AuthStep] map {
      case RedirectTo(url) => Redirect(url)
    }
  }
}
