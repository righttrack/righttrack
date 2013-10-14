/// <reference path="../../lib/node-uuid.d.ts" />

interface UUIDGenerator {
  random(): string
}

module common {
  export module services {

    export class NodeUUIDGenerator implements UUIDGenerator {

      constructor() {}

      random(): string {
        return uuid.v4();
      }
    }

    export var idGenerator: UUIDGenerator = new NodeUUIDGenerator();
  }
}