package cake

import scala.concurrent.ExecutionContext

/**
 * A trait for self-typing when you need an execution context.
 */
trait HasExecutionContext {

  implicit def executionContext: ExecutionContext
}

/**
 * Mixin to provide the global execution context.
 */
trait GlobalExecutionContext extends HasExecutionContext {

  implicit def executionContext: ExecutionContext = ExecutionContext.global
}