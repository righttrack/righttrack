package services.user.auth

import akka.actor._
import cake.GlobalExecutionContext
import com.google.inject.Inject
import database.dao.user.UserDAO
import database.dao.{NotCreated, Created}
import models.EntityIdGenerator
import models.auth._
import models.users.UserId
import scala.util.Failure
import scala.util.Success
import services.user.auth.oauth.OAuthAgent

class AuthService @Inject() (idGen: EntityIdGenerator, userDAO: UserDAO)
  extends Actor
  with ActorLogging
  with GlobalExecutionContext {

  private[this] var agents = Map[UserId, ActorRef]()

  override def receive: Receive = {

    case AuthService.BeginAuthorization(userId, params) => params match {
      case OAuthParams(state) =>
        val agent = context.actorOf(Props[OAuthAgent], s"OAuthAgent-for-${ userId.value }")
        log.debug(s"Adding agent: $agent")
        agents += userId -> agent
        agent ! OAuthAgent.WaitForToken(sender, userId, state)
    }

    case AuthService.ConfirmAuthorization(userId, confirm) => confirm match {
      case OAuthConfirmation(token, state) => agents.get(userId) match {
        case Some(agent) => agent ! OAuthAgent.WaitForToken
        case None => sender ! AuthService.OAuthTimeout
      }
    }

    case AuthService.AccountAuthorized(userId, account) =>
      userDAO.linkAccount(userId, account) onComplete {
        case Success(Created(it)) => sender ! AuthService.AccountLinked(userId, it.id)
        case Success(NotCreated(e)) => log.error(e, s"Could not save authenticated account for $userId")
        case Failure(e) => log.error(e, s"Could not reach Database to Authorize OAuth account for $userId")
      }

  }
}

object AuthService {

  import language.existentials

  /**
   * Marks all potential authorization steps required for linking an account to oauth.
   */
  sealed trait AuthResult

  case class RedirectTo(url: String) extends AuthResult

  case object OAuthTimeout

  /**
   * Begin the authorization process.
   *
   * @param params any params required to begin the authentication process
   */
  case class BeginAuthorization(userId: UserId, params: AuthParams[_ <: AuthAccount])

  case class ConfirmAuthorization(userId: UserId, confirm: AuthConfirmation[_ <: AuthAccount])

  case class AccountAuthorized(userId: UserId, account: AuthAccount)

  case class AccountLinked(userId: UserId, account: AuthAccountId)

}

/**
 * Represents the parameters required to link the account, along with the account information.
 */
sealed trait AuthParams[Account <: AuthAccount]

case class OAuthParams(state: String) extends AuthParams[OAuthAccount]

sealed trait AuthConfirmation[Account <: AuthAccount]

case class OAuthConfirmation(token: OAuthToken, state: String) extends AuthConfirmation[OAuthAccount]