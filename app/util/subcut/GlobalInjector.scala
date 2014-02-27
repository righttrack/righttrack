package util.subcut

import settings.Global

trait GlobalInjector {

  implicit protected val injector = GlobalInjector.injector
}

object GlobalInjector {

  implicit final val injector = Global.injector
}
