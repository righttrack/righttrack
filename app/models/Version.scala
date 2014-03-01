package models

class Version private[Version] (val number: Int)

// TODO: Figure out how to use this with serializers and storage in the database when needed
object Version {

  def unapply(number: Int): Option[Version] = number match {
    case 1 => Some(v1)
    case _ => None
  }

  class V1 extends Version(1)
  implicit val v1: V1 = new V1()
}
