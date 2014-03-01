package models.activity.verb

import models.Version._
import models.users.UserId
import models.tasks.TaskId

trait Creates extends Verb

object Creates {

  val creates: Creates = new Verb("creates") with Creates

  implicit object UserCanCreateTaskInV1 extends ValidAction[UserId, Creates, TaskId, V1]
}
