package modules

import com.escalatesoft.subcut.inject.NewBindingModule
import services.UserService
import database.dao.UserDAO

object SubServicesModule extends NewBindingModule(module => {
  import module._

  bind[UserService] toModuleSingle { implicit module =>
    new UserService(inject[UserDAO](None))
  }
})
