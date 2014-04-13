package controllers.api

import models.tasks.{TaskId, Task}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import serializers.api.TaskSerializers

class TaskController extends Controller {

  import TaskSerializers._

  def get = Action {
    val json = Json.toJson(Task(TaskId("1"), "heyo!"))
    val task = Json.fromJson[Task](json)
    Ok(json)
  }
}
