package modules

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import services.UserService

object ServicesModule extends AbstractModule with ScalaModule {

  def configure() {
    bind[UserService]
  }

}
