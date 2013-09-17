package controllers.api

import play.api.libs.json.Json._
import play.api.mvc.{Action, Controller}
import javax.inject.{Inject, Singleton}
import services.UUIDGenerator
import models.users.User
import models.common.Email

@Singleton
class UserRestController @Inject() (idGen: UUIDGenerator) extends Controller {

  import models.users.Serializers._

  def todo = TODO

  def get = Action {
    Ok(toJson(User(idGen.next(), Email("kyle.tester@mailinator.com"), "Kyle")))
  }

}