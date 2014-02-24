package modules

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import services.UserService
import database.dao.GithubPushEventDAO
import database.mongo.GithubPushCollection
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.api.{MongoDriver, MongoConnection, DB}

object ServicesModule extends AbstractModule with ScalaModule {

  lazy val driver = new MongoDriver
  lazy val connection =  driver.connection(List("localhost"))
  lazy val db = connection("righttrack")
  lazy val coll = new BSONCollection(db())

  def configure() {
    bind[UserService]
    bind[GithubPushEventDAO].toInstance(new GithubPushCollection(coll))
  }

}
