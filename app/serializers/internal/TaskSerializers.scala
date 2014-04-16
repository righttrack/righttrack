package serializers.internal

import serializers._

object TaskIdSerializers extends InternalEntityIdFormat
with generic.TaskIdSerializers

object TaskSerializers extends InternalEntityIdFormat
with generic.TaskSerializers
