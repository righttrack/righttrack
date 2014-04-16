package serializers.internal

import serializers._

object AuthIdSerializers extends InternalEntityIdFormat
with generic.AuthIdSerializers

object AuthSerializers extends InternalEntityIdFormat
  with generic.AuthIdSerializers
  with generic.UserIdSerializers