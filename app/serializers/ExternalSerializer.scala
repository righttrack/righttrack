package serializers

import play.api.libs.json.{Writes, Format, Reads}

sealed trait ExternalSerializer[T] extends DefaultSerializerFormats {

  /**
   * Define an implicit internal format for logging and general re-use.
   */
  implicit def internal: Format[T]
}

trait ExternalReader[T] extends ExternalSerializer[T] {

  def external: Reads[T]
}

trait ExternalWriter[T] extends ExternalSerializer[T] {

  def external: Writes[T]
}

trait ExternalFormat[T] extends ExternalReader[T] with ExternalWriter[T] {

  override def external: Format[T]
}

