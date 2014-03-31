package controllers.api

import play.api.mvc.{Action, Controller}
import cake.{GlobalInjector, GlobalExecutionContext, GlobalActorSystem}
import akka.actor.Props
import services.user.auth.AuthService
import akka.pattern.ask
import models.users.UserId
import services.user.auth.AuthService.{RedirectTo, AuthResult}
import models.auth.OAuthAccount
import com.google.inject.Singleton

@Singleton
class AuthController
  extends Controller
  with GlobalActorSystem
  with GlobalExecutionContext
  with GlobalInjector
  with DefaultTimeouts {

  // TODO: Figure out why multiple auth services are getting created
  private[this] val authService = actorSystem.actorOf(Props(injector.instance[AuthService]))

  // TODO: Match on kind to get the authentication method and service name
  def link(id: String, name: String, method: String) = Action.async {
    (authService ? AuthService.Authorize(UserId(id), classOf[OAuthAccount])).mapTo[AuthResult] map {
      case RedirectTo(url) => Redirect(url)
    }
  }
}
