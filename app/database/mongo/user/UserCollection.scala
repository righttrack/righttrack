package database.mongo.user

import com.google.inject.{Singleton, Inject}
import database.dao.user.UserDAO
import database.mongo.BaseCollection
import models.common.Email
import models.users.User
import models.users.UserId
import play.api.libs.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.DB

@Singleton
class UserCollection @Inject()(collection: JSONCollection, serializers: UserMongoSerializers)
  extends BaseCollection
  with UserDAO {

  //  private[this] implicit def writeUser = serializers.user

  import UserMongoSerializers._

  override def create(user: User): Creates[User] = collection.insertResult(user)

  override def findByEmail(email: Email): FindsOne[User] =
    collection.find(Json.obj("email" -> JsString(email.address))).one[User]

  override def findById(id: UserId): FindsOne[User] =
    collection.find(id).one[User]
}

object UserCollection {

  def apply(db: DB): UserCollection = {
    new UserCollection(db("user"), UserMongoSerializers)
  }
}