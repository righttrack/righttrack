package database.slick.h2.connection

import scala.slick.session.Database

class H2InMemoryDBProvider(name: String) extends DatabaseProvider {

  // A helper constructor for tests
  def this(cls: Class[_]) = this(cls.getName)

  def database = Database.forURL(s"jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

}
