package modules

import services.UUIDGenerator
import services.impl.JavaUUIDGenerator
import database.slick.h2.connection.{CoreDatabase, DatabaseProvider}
import database.dao.UserDAO
import com.escalatesoft.subcut.inject.NewBindingModule
import database.slick.h2.dao.H2UserDAO

object SubH2DatabaseModule extends NewBindingModule(module => {
  import module._

  // Make this an early common module?
  bind[UUIDGenerator] toSingle new JavaUUIDGenerator

  // Bind the database
  bind[DatabaseProvider] toSingle new CoreDatabase

  // Bind all the DAOs
  bind[UserDAO] toModuleSingle { implicit module =>
    new H2UserDAO(inject[DatabaseProvider](None))
  }
})
