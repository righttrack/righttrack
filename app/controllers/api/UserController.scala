package controllers.api

import javax.inject.{Inject, Singleton}
import models.EntityIdGenerator
import play.api.libs.json.Json._
import play.api.mvc.{Action, Controller}
import services.UserService
import play.api.libs.json._
import scala.concurrent.Future
import cake.GlobalExecutionContext
import models.users.UserId
import models.users.User
import play.api.libs.json.JsSuccess
import models.users.Name
import models.common.Email

@Singleton
class UserController @Inject()(idGen: EntityIdGenerator, users: UserService)
  extends Controller
  with GlobalExecutionContext {

  def todo = TODO

  def get = Action {
    Ok(toJson(User(idGen next UserId, "kyledude", Email("kyle.tester@mailinator.com"), Name("kyle", "dude"))))
  }

  def createUser = Action.async(parse.json) { request =>
    fromJson[User](request.body) match {
      case JsSuccess(user, _) =>
        users.create(user).map(_ => Created)
      case JsError(locatedErrors) => Future {
        BadRequest(JsError.toFlatJson(locatedErrors))
      }
    }
  }

}