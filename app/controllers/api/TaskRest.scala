package controllers.api

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import models.tasks.{TaskId, Task, TaskSerializers}

class TaskRest extends Controller {

  import TaskSerializers._

  def get = Action {
    val json = Json.toJson(Task(TaskId("1"), "heyo!"))
    val task = Json.fromJson[Task](json)
    Ok(json)
  }
}
