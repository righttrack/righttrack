package serializers.mongo

import serializers._

trait UserIdSerializers extends MongoEntityIdFormat
with generic.UserIdSerializers

object UserSerializers extends MongoEntityIdFormat
with generic.UserSerializers