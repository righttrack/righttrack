package models.users

import models.EntityModel
import models.common.Email

case class User(id: String, email: Email, name: String) extends EntityModel
