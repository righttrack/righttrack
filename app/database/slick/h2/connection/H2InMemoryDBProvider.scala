package database.slick.h2.connection

import scala.slick.session.Database

class H2InMemoryDBProvider(name: String) extends DatabaseProvider {

  def database = Database.forURL("jdbc:h2:mem:" + name, driver = "org.h2.Driver")

}
