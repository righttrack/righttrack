package services.user.auth

import akka.actor.{ActorLogging, Props, Actor}
import cake.GlobalExecutionContext
import database.dao.user.UserDAO
import database.dao.{NotCreated, Created}
import models.auth._
import models.meta.EntityTypes
import models.users.UserId
import scala.reflect.ClassTag
import scala.util.{Failure, Success}
import services.EntityIdGenerator
import services.user.auth.oauth.OAuthAgent

class AuthService(idGen: EntityIdGenerator, userDAO: UserDAO)
  extends Actor
  with ActorLogging
  with GlobalExecutionContext {

  val oauth = context.actorOf(Props[OAuthAgent])

  override def receive: Receive = {

    case AuthService.Authorize(userId, method) => method.getName match {
      case EntityTypes.OAuthAccount.className =>
        oauth ! OAuthAgent.WaitForToken(sender, userId)
    }

    case AuthService.ConfirmAuthorization(params) => params match {
      case it: OAuthAgent.OAuthParams => oauth ! it
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
  sealed trait AuthStep

  case class RedirectTo(url: String) extends AuthStep

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