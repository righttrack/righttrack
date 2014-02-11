package database.dao

import models.tasks.{Task, TaskId}
import scala.slick.jdbc.GetResult
import models.results.{DatabaseException, CreateResult}

trait TaskDAO {

  def add(task: Task): CreateResult[DatabaseException, Task]

  def get(id: TaskId): GetResult[Task]
}
