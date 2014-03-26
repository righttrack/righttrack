package modules

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import services.UUIDGenerator
import services.impl.JavaUUIDGenerator
import database.slick.h2.dao.H2UserDAO
import database.slick.h2.connection.DatabaseProvider
import database.slick.h2.connection.CoreDatabase
import database.dao.user.UserDAO

object H2DatabaseModule extends AbstractModule with ScalaModule {

  def configure() {
    // Make this an early common module?
    bind[UUIDGenerator].to[JavaUUIDGenerator]

    // Bind the database
    bind[DatabaseProvider].to[CoreDatabase]

    // Bind all the DAOs
    bind[UserDAO].to[H2UserDAO]
  }

}
