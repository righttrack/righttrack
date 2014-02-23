package database.dao

import models.tasks.{Task, TaskId}
import scala.concurrent.Future

// TODO: Update this to work with CreateResults

trait TaskDAO {

  def add(task: Task): Future[Boolean]

  def findById(id: TaskId): Future[Option[Task]]
}
