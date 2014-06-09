package serializers.internal

import serializers._

object AuthIdSerializers extends InternalSerializerFormat
with generic.AuthIdSerializers

object AuthSerializers extends InternalSerializerFormat
  with generic.AuthIdSerializers
  with generic.UserIdSerializers