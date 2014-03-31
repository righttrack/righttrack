package database.dao

import models.Entity
import play.api.libs.iteratee.Enumerator
import scala.concurrent.Future

trait BaseDAO {

  import scala.language.higherKinds

  type CreateResultType[T <: Entity] <: CreateResult[T]

  final type Creates[T <: Entity] = Future[CreateResultType[T]]
  final type Finds[T] = Enumerator[T]
  final type FindsOne[T] = Future[Option[T]]
}
