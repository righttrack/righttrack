package modules

import cake.HasInjector
import com.google.inject.util.Modules
import com.google.inject.{Guice, AbstractModule}
import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import play.api.{Application, GlobalSettings}
import scala.reflect.ClassTag

/**
 * Set up the Guice injector and provide the mechanism for return objects from the dependency graph.
 */
object Global extends GlobalSettings with HasInjector {

  /**
   * Bind types based on the abstract module definition.
   */
  val injector: ScalaInjector = new ScalaInjector(Guice.createInjector(
    new AbstractModule {
      def configure() {
        this install Modules.combine(
          new MongoGuice(LocalMongoConfig),
          ServicesModule
        )
      }
    }
  ))

  /**
   * Controllers must be resolved through the application context. There is a special method of GlobalSettings
   * that we can override to resolve a given controller. This resolution is required by the Play router.
   */
  override def getControllerInstance[A](controllerClass: Class[A]): A =
    injector.instance(Manifest.classType[A](controllerClass))

  override def onStart(app: Application) {
  }
}