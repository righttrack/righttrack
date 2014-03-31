package models.auth

import models.meta.{EntityType, EntityTypes}
import models.users.UserId
import models.{Entity, EntityId}
import org.joda.time.DateTime
import scala.util.{Failure, Success, Try}

sealed trait AuthAccount extends Entity {

//  def userId: UserId
}
//
//object AuthAccount {
//
//  object Types {
//
//    import scala.reflect.classTag
//
//    val OAuthAccount = classTag[OAuthAccount]
//  }
//}

case class OAuthAccountId(value: String) extends AnyVal with EntityId {
  override def entityType: EntityType = EntityTypes.OAuthAccount
}

case class OAuthAccount(id: OAuthAccountId, token: OAuthToken)
  extends AuthAccount
  with Entity

case class OAuthToken(
  access: String,
  refresh: String,
  expires: DateTime,
  scopes: Set[String],
  kind: OAuthToken.TokenType
)

object OAuthToken {
  type TokenType = TokenType.Value

  object TokenType extends Enumeration {

    object Bearer extends Val("bearer")

    @inline final def tryFind(name: String): Try[TokenType] = find(name) match {
      case Some(found) => Success(found)
      case None => Failure(new IllegalArgumentException(s"Unrecognized token type: $name"))
    }

    @inline final def find(name: String): Option[TokenType] = values.find(_.toString == name)
  }
}