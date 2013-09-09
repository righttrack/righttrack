package database.slick.h2.dao

import database.slick.h2.connection.DatabaseProvider
import scala.slick.driver.H2Driver.profile.SimpleQL

abstract class H2DAO(dbProvider: DatabaseProvider) extends SimpleQL {

  def db = dbProvider.database

}
