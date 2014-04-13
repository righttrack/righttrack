package database.mongo.auth

import database.dao.auth.AuthAccountDAO
import database.mongo.BaseCollection
import models.auth.AuthAccount
import play.modules.reactivemongo.json.collection.JSONCollection
import models.users.UserId
import database.mongo.user.UserCollection
import serializers.api.AuthSerializers

class AuthAccountCollection(userCollection: UserCollection) extends BaseCollection with AuthAccountDAO {

  import AuthSerializers._
//
//  override def create[Account <: AuthAccount](userId: UserId, account: Account): Creates[Account] =
//    collection.insertResult(account)

}
