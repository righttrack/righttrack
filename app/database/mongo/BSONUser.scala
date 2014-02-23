package database.mongo

import reactivemongo.bson.{BSONString, BSONDocument, BSONDocumentWriter}
import models.users.User

object BSONUser {

  def writer: BSONDocumentWriter[User] = Implicits.userDocWriter

  object Implicits {

    implicit val userDocWriter = new BSONDocumentWriter[User] {

      override def write(user: User): BSONDocument = BSONDocument(Seq(
        "id" -> BSONString(user.id.value),
        "email" -> BSONString(user.email.address),
        "name" -> BSONString(user.name)
      ))
    }
  }
}
