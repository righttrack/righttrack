/* /// <reference path="./bower_source/node-uuid.js"/> */

interface UUID {
  v1(options?: Object, buf?: Array, offset?: Number): string
  v2(options?: Object, buf?: Array, offset?: Number): string
  v3(options?: Object, buf?: Array, offset?: Number): string
  v4(options?: Object, buf?: Array, offset?: Number): string
}

declare var uuid: UUID;
