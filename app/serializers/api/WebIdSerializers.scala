package serializers.api

import serializers.EntityIdSerializers

trait WebIdSerializers
  extends AuthIdSerializers
  with GithubIdSerializers
  with UserIdSerializers {
  self: EntityIdSerializers =>
}

object WebIdSerializers extends EntityIdSerializers with WebIdSerializers
