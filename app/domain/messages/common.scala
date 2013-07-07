package domain

package object common {

  trait Message

  case class Email(address: String) extends Message

}
