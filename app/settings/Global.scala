package settings

import modules.{SubH2DatabaseModule, SubServicesModule}
import play.api.{Application, GlobalSettings}

/**
 * Set up the Guice injector and provide the mechanism for return objects from the dependency graph.
 */
object Global extends GlobalSettings {

  /**
   * Bind types based on the abstract module definition.
   */
//  val injector = Guice.createInjector(new AbstractModule {
//    def configure() {
//      this install Modules.combine(
//        H2DatabaseModule,
//        ServicesModule
//      )
//    }
//  })
  val injector = SubServicesModule ~ SubH2DatabaseModule

  /**
   * Controllers must be resolved through the application context. There is a special method of GlobalSettings
   * that we can override to resolve a given controller. This resolution is required by the Play router.
   */
  override def getControllerInstance[A](controllerClass: Class[A]): A =
    injector.inject(Some(s"Controller: ${controllerClass.getCanonicalName}"))

  override def onStart(app: Application) {
  }
}