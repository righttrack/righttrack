package serializers.internal

import serializers._

object UserIdSerializers extends InternalSerializerFormat
with generic.UserIdSerializers

object UserSerializers extends InternalSerializerFormat
with generic.UserSerializers
with generic.UserIdSerializers
