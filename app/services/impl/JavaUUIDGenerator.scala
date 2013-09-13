package services.impl

import services.UUIDGenerator

class JavaUUIDGenerator extends UUIDGenerator {
  import java.util.UUID

  def next(): String = UUID.randomUUID().toString

}
