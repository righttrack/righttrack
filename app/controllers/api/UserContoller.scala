package controllers.api

import javax.inject.{Inject, Singleton}
import models.common.Email
import models.users.{UserId, User}
import play.api.libs.json.Json._
import play.api.mvc.{Action, Controller}
import services.UUIDGenerator

@Singleton
class UserContoller @Inject()(idGen: UUIDGenerator) extends Controller {

  import models.users.UserSerializers._

  def todo = TODO

  def get = Action {
    Ok(toJson(User(UserId(idGen.next()), Email("kyle.tester@mailinator.com"), "Kyle")))
  }

}