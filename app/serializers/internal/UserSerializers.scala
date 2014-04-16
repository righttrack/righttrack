package serializers.internal

import serializers._

object UserIdSerializers extends InternalEntityIdFormat
with generic.UserIdSerializers

object UserSerializers extends InternalEntityIdFormat
with generic.UserSerializers
with generic.UserIdSerializers
