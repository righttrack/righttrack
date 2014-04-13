package modules

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import services.UserService
import database.dao.GithubPushEventDAO
import database.mongo.GithubPushCollection
import reactivemongo.api.{MongoDriver, DB}
import scala.concurrent.ExecutionContext.Implicits.global
import play.modules.reactivemongo.json.collection.JSONCollection
import services.user.auth.AuthService
import models.{JavaUUIDGenerator, EntityIdGenerator}
import controllers.api.AuthController

object ServicesModule extends AbstractModule with ScalaModule {

  lazy val driver = new MongoDriver
  lazy val connection =  driver.connection(List("localhost"))
  lazy val db: DB = connection("righttrack")
  lazy val coll: JSONCollection = db("GithubPushEvent")

  def configure() {
    bind[EntityIdGenerator] toInstance JavaUUIDGenerator
    bind[AuthController]
    bind[AuthService]
    bind[UserService]
    bind[GithubPushEventDAO] toInstance new GithubPushCollection(coll)
  }

}
