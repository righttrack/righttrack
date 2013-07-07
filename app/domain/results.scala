import scala.util.Try

package object results {

  case class CreateResult[T](created: Boolean, error: Try[Boolean], item: T)

}
