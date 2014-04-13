package controllers.api

import javax.inject.{Inject, Singleton}
import models.EntityIdGenerator
import models.common.Email
import models.users.{UserId, User}
import play.api.libs.json.Json._
import play.api.mvc.{Action, Controller}
import serializers.api.UserSerializers

@Singleton
class UserContoller @Inject()(idGen: EntityIdGenerator) extends Controller {

  import UserSerializers._

  def todo = TODO

  def get = Action {
    Ok(toJson(User(idGen next UserId, Email("kyle.tester@mailinator.com"), "Kyle")))
  }

}