import com.google.inject.util.Modules
import com.google.inject.{Guice, AbstractModule}
import modules.{ServicesModule, H2DatabaseModule}
import play.api.{Application, GlobalSettings}

/**
 * Set up the Guice injector and provide the mechanism for return objects from the dependency graph.
 */
object Global extends GlobalSettings {

  /**
   * Bind types based on the abstract module definition.
   */
  val injector = Guice.createInjector(new AbstractModule {
    def configure() {
      this install Modules.combine(
        H2DatabaseModule,
        ServicesModule
      )
    }
  })

  /**
   * Controllers must be resolved through the application context. There is a special method of GlobalSettings
   * that we can override to resolve a given controller. This resolution is required by the Play router.
   */
  override def getControllerInstance[A](controllerClass: Class[A]): A = injector.getInstance(controllerClass)

  def initializeOnStart(app: Application): Unit = {
//    import scala.reflect.runtime.universe._
//    val mirror = runtimeMirror(app.classloader)
//    val pkg = mirror.staticPackage("database.slick.h2.table")
//    val sig = pkg.typeSignature
  }

  override def onStart(app: Application) {
    initializeOnStart(app)
  }
}