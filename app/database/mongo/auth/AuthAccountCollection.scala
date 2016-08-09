package database.mongo.auth

import database.dao.auth.AuthAccountDAO
import database.mongo.BaseCollection
import database.mongo.user.UserCollection

class AuthAccountCollection(userCollection: UserCollection) extends BaseCollection with AuthAccountDAO {

//
//  override def create[Account <: AuthAccount](userId: UserId, account: Account): Creates[Account] =
//    collection.insertResult(account)

}
