package database.mongo

import database.dao.TaskDAO
import models.tasks.{TaskId, Task}
import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.bson._
import reactivemongo.api.collections.default.BSONCollection

class TaskCollection(collection: BSONCollection) extends TaskDAO {

  override def findById(id: TaskId)(implicit writer: BSONDocumentWriter[Task], ctx: ExecutionContext): Future[Option[Task]] = {
    ???
  }

  override def add(task: Task)(implicit writer: BSONDocumentWriter[Task], ctx: ExecutionContext): Future[Boolean] = {
    collection.insert(writer.write(task)) map (_.ok)
  }
}
