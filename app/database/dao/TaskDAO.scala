package database.dao

import models.tasks.{Task, TaskId}
import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.bson.BSONDocumentWriter

trait TaskDAO {

  def add(task: Task)(implicit writer: BSONDocumentWriter[Task], ctx: ExecutionContext): Future[Boolean]

  def findById(id: TaskId)(implicit writer: BSONDocumentWriter[Task], ctx: ExecutionContext): Future[Option[Task]]
}
