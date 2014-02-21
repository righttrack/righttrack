package models.activity.verb

import models.Version._
import models.users.UserId
import models.tasks.TaskId

class Creates extends Verb("creates")

object Creates {

  val creates: Creates = new Creates()

  implicit object UserCanCreateTaskInV1 extends ValidAction[UserId, Creates, TaskId, V1]
}
