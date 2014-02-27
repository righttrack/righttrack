package modules

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

object H2DatabaseModule extends AbstractModule with ScalaModule {

  def configure() {
    // Make this an early common module?
//    bind[UUIDGenerator].to[JavaUUIDGenerator]

    // Bind the database
//    bind[DatabaseProvider].to[CoreDatabase]

    // Bind all the DAOs
//    bind[UserDAO].to[H2UserDAO]
  }

}
