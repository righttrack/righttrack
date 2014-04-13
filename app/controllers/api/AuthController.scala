package controllers.api

import akka.actor.Props
import akka.pattern.ask
import cake._
import com.google.inject.Singleton
import models.users.UserId
import play.api.mvc.{Action, Controller}
import scala.annotation.switch
import services.user.auth.AuthService
import services.user.auth.AuthService.{BeginAuthorization, AuthResult, RedirectTo}
import services.user.auth.OAuthParams

@Singleton
class AuthController
  extends Controller
  with GlobalActorSystem
  with GlobalExecutionContext
  with GlobalInjector
  with DefaultIdGen
  with DefaultTimeouts {

  // TODO: Figure out why multiple auth services are getting created
  private[this] val authService = actorSystem.actorOf(Props(injector.instance[AuthService]))

  // TODO: Match on kind to get the authentication method and service name
  def link(userId: String, name: String) = Action.async {
    val params = (name: @switch) match {
      case "github" => OAuthParams(idGen.next())
    }
    (authService ? BeginAuthorization(UserId(userId), params)).mapTo[AuthResult] map {
      case RedirectTo(url) => Redirect(url)
    }
  }
}
