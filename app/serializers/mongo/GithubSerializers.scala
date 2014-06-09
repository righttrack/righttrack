package serializers.mongo

import serializers.MongoSerializerFormat
import serializers.generic

object GithubIdSerializers extends MongoSerializerFormat
with generic.GithubIdSerializers

object GithubSerializers extends MongoSerializerFormat
with generic.GithubSerializers
