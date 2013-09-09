package models.users

import models.FullModel
import models.common.Email

case class User(email: Email, name: String) extends FullModel

//case class User(id: Option[String], email: Email, name: String) extends FullModel with Entity[Int]

