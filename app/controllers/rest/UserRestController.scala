package controllers.rest

import models.users.AnonUser
import play.api.libs.json.Json._
import play.api.mvc.{Action, Controller}
import javax.inject.Singleton

@Singleton
class UserRestController extends Controller {

  import models.users.Serializers._

  def todo = TODO

  def get = Action {
    Ok(toJson(AnonUser))
  }

}