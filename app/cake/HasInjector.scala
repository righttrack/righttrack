package cake

import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import modules.Global

trait HasInjector {

  def injector: ScalaInjector
}

trait GlobalInjector extends HasInjector {

  override def injector: ScalaInjector = Global.injector
}
