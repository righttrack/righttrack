package database.mongo

import database.dao.GithubPushEventDAO
import reactivemongo.api.collections.default.BSONCollection


class GithubPushCollection(collection: BSONCollection)
  extends BaseCollection
  with GithubPushEventDAO {

  override def add: Unit = ???

}
