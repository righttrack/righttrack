package serializers.mongo

import serializers._

trait UserIdSerializers extends MongoSerializerFormat
with generic.UserIdSerializers

object UserSerializers extends MongoSerializerFormat
with generic.UserSerializers