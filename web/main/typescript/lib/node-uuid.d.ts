///<reference path="bower_source/node-uuid.js"/>

interface UUID {
  v1(options?: Object, buf?: Array, offset?: Number): String
  v2(options?: Object, buf?: Array, offset?: Number): String
  v3(options?: Object, buf?: Array, offset?: Number): String
  v4(options?: Object, buf?: Array, offset?: Number): String
}

declare var uuid: UUID;
