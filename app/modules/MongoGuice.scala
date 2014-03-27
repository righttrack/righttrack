package modules

import cake._
import com.google.inject.AbstractModule
import database.dao.user.UserDAO
import database.mongo.user.UserCollection
import net.codingwell.scalaguice.ScalaModule
import reactivemongo.api.{DB, MongoConnection, MongoDriver}

// TODO: Move this configuration into config files

trait MongoConfig {
  self: HasActorSystem with HasExecutionContext =>

  def driver: MongoDriver

  def connection: MongoConnection

  def db: DB
}

class LocalMongoConfig extends MongoConfig with GlobalActorSystem with GlobalExecutionContext {

  override def driver: MongoDriver = MongoDriver(actorSystem)

  override def connection: MongoConnection = driver.connection(List("localhost"))

  override def db: DB = connection("righttrack")
}

object LocalMongoConfig extends LocalMongoConfig

class MongoGuice(config: MongoConfig) extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    bind[UserDAO] toInstance UserCollection(config.db)
  }
}
