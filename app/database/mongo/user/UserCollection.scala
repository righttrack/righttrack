package database.mongo.user

import com.google.inject.{Singleton, Inject}
import database.dao.user.UserDAO
import database.mongo._
import models.auth.AuthAccount
import models.common.Email
import models.users.User
import models.users.UserId
import play.api.libs.json._
import reactivemongo.api.DB
import reactivemongo.api.collections.default.BSONCollection

@Singleton
class UserCollection @Inject()(collection: BSONCollection)
  extends BaseCollection
  with UserDAO {

  implicit val formatUserId: MongoFormat[UserId] = Bson.formatId(UserId)
  implicit val formatUser: MongoOFormat[User] = Bson.formatEntity[User]

    // todo: flatten the id into _id with
  // val userWrites = implicitly[Writes[User]].transform( js => js.as[JsObject] - "id"  ++ Json.obj("_id" -> js \ "id") )

  override def create(user: User): Creates[User] = {
    collection.createEntity(user)
  }

  override def findByEmail(email: Email): FindsOne[User] =
    collection.find(Bson.obj("email" -> JsString(email.address))).one[User]

  override def findById(id: UserId): FindsOne[User] =
    collection.findById(id)

  override def linkAccount[Account <: AuthAccount](userId: UserId, account: Account): Creates[Account] =
    collection.update(Bson.obj("_id" -> userId.value), Bson.obj("auth" -> JsArray(Seq(Json.toJson(account))))) map {
      last => if (last.inError) MongoError(last) else MongoCreated(account)
    }
}

object UserCollection {

  def apply(db: DB): UserCollection = {
    new UserCollection(db("user"))
  }
}