package database.dao

import models.Entity
import play.api.libs.iteratee.Enumerator
import scala.concurrent.Future

trait BaseDAO extends BaseDAOTypes {

}

trait BaseDAOTypes {

  import scala.language.higherKinds

  type Creates[T <: Entity] = Future[CreateResult[T]]
  type Finds[T] = Enumerator[T]
  type FindsOne[T] = Future[Option[T]]
}

object BaseDAOTypes extends BaseDAOTypes
