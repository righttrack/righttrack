package database.slick.h2.connection

import scala.slick.session.Database

trait DatabaseProvider {

  def database: Database

}
