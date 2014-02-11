package models.users

import models.StringEntityModel
import models.common.Email

case class User(id: String, email: Email, name: String) extends StringEntityModel
