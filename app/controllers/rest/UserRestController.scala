package controllers.rest

import play.api.libs.json.Json._
import play.api.mvc.{Action, Controller}
import models.user.AnonUser
import javax.inject.Singleton

@Singleton
class UserRestController extends Controller {

  import models.user.Serializers._

  def todo = TODO

  def get = Action {
    Ok(toJson(AnonUser))
  }

}