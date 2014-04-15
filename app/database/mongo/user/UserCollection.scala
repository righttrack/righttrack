package database.mongo.user

import com.google.inject.{Singleton, Inject}
import database.dao.user.UserDAO
import database.mongo.{MongoError, MongoCreated, BaseCollection}
import models.common.Email
import models.users.User
import models.users.UserId
import play.api.libs.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.DB
import models.auth.AuthAccount
import serializers.mongo.MongoUserSerializers
import serializers.api.AuthSerializers

@Singleton
class UserCollection @Inject()(collection: JSONCollection)
  extends BaseCollection
  with UserDAO {

  // TODO: Allow extending serializers with intertwined ids better
  import MongoUserSerializers._
  import AuthSerializers.authAccountFormat

  // todo: flatten the id into _id with
  // val userWrites = implicitly[Writes[User]].transform( js => js.as[JsObject] - "id"  ++ Json.obj("_id" -> js \ "id") )

  override def create(user: User): Creates[User] =
    collection.insertResult(user)

  override def findByEmail(email: Email): FindsOne[User] =
    collection.find(Json.obj("email" -> JsString(email.address))).one[User]

  override def findById(id: UserId): FindsOne[User] =
    collection.find(id).one[User]

  override def linkAccount[Account <: AuthAccount](userId: UserId, account: Account): Creates[Account] =
    collection.update(Json.obj("_id" -> userId.value), Json.obj("auth" -> JsArray(Seq(Json.toJson(account))))) map {
      last => if (last.inError) MongoError(last) else MongoCreated(account)
    }
}

object UserCollection {

  def apply(db: DB): UserCollection = {
    new UserCollection(db("user"))
  }
}