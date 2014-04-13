package models.auth

import models.meta.{EntityType, EntityTypes}
import models.{Entity, EntityId}
import org.joda.time.DateTime
import scala.util.{Failure, Success, Try}

sealed trait AuthAccount extends Entity {

  override def id: AuthAccountId
}

case class AuthAccountId(value: String) extends AnyVal with EntityId {
  override def entityType: EntityType = EntityTypes.OAuthAccount
}

case class OAuthAccount(id: AuthAccountId, token: OAuthToken)
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

    val Bearer = Value("bearer")

    @inline final def tryFind(name: String): Try[TokenType] = find(name) match {
      case Some(found) => Success(found)
      case None => Failure(new IllegalArgumentException(s"Unrecognized token type: $name"))
    }

    @inline final def find(name: String): Option[TokenType] = values.find(_.toString == name)
  }
}