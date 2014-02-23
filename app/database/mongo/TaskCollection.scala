package database.mongo

import database.dao.TaskDAO
import models.tasks.{TaskId, Task}
import scala.concurrent.Future
import reactivemongo.bson._
import reactivemongo.api.collections.default.BSONCollection

class TaskCollection(collection: BSONCollection)
  extends BaseCollection
  with TaskDAO {

  override def findById(id: TaskId): Future[Option[Task]] = {
    ???
  }

  override def add(task: Task): Future[Boolean] = {
    collection.insert(BSONTask.writer.write(task)) map (_.ok)
  }
}

object BSONTask {

  def writer: BSONDocumentWriter[Task] = Implicits.taskDocWriter

  object Implicits {

    implicit val taskDocWriter = new BSONDocumentWriter[Task] {

      override def write(task: Task): BSONDocument = BSONDocument(Seq(
        "id" -> BSONString(task.id.value),
        "description" -> BSONString(task.description),
        "completed" -> BSONBoolean(task.completed)
      ))
    }
  }
}
