package database.dao

import models.Entity
import play.api.libs.iteratee.Enumerator
import scala.concurrent.Future

trait BaseDAO {

  import scala.language.higherKinds

  type CreateResultType[T <: Entity] <: CreateResult[T]

  type Creates[T <: Entity] = Future[CreateResultType[T]]
  type Finds[T] = Enumerator[T]
  type FindsOne[T] = Future[Option[T]]
}
