package services.user.auth

import akka.actor._
import cake.GlobalExecutionContext
import com.google.inject.Inject
import database.dao.user.UserDAO
import database.dao.{NotCreated, Created}
import models.EntityIdGenerator
import models.auth._
import models.meta.EntityTypes
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

    case AuthService.Authorize(userId, method) => method.getName match {
      case EntityTypes.OAuthAccount.className =>
        val agent = context.actorOf(Props(new OAuthAgent), s"OAuthAgent-${ userId.value }")
        agents += userId -> agent
        agent ! OAuthAgent.WaitForToken(sender, userId)
    }

    case AuthService.ConfirmAuthorization(params) => params match {
      case it: OAuthAgent.OAuthParams => agents.get(params.userId) match {
        case Some(agent) => agent ! params
        case None => sender ! AuthService.OAuthTimeout
      }
      case it => log.error(
        s"Unrecognized authorization parameters type for user '${params.userId}': ${it.getClass.getName}"
      )
    }

    case AuthService.AccountAuthorized(params) =>
      userDAO.linkAccount(params.userId, params.account) onComplete {
        case Success(Created(it)) => sender ! AuthService.AccountLinked(it)
        case Success(NotCreated(e)) => log.error(e, s"Could not save authenticated account for ${params.userId}")
        case Failure(e) => log.error(e, s"Could not reach Database to Authorize OAuth account for ${params.userId}")
      }

  }
}

object AuthService {

  /**
   * Marks all potential authorization steps required for linking an account to oauth.
   */
  sealed trait AuthResult

  case class RedirectTo(url: String) extends AuthResult

  case object OAuthTimeout

  /**
   * Begin the authorization process.
   *
   * @param userId the user to authorize an account for
   * @param method the method of authorization
   */
  case class Authorize[T <: AuthAccount](userId: UserId, method: Class[T])

  case class ConfirmAuthorization(params: AuthParams)

  case class AccountAuthorized(params: AuthParams)

  case class AccountLinked(account: AuthAccount)

}

/**
 * Represents the parameters required to link the account, along with the account information.
 */
trait AuthParams {

  /**
   * The specific type of account to authorize.
   */
  type Account <: AuthAccount

  /**
   * User's account to authorize.
   */
  def userId: UserId

  /**
   * The account to link if these parameters are valid.
   */
  def account: Account
}