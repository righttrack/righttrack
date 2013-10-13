///<reference path="../lib/angular.d.ts"/>
///<reference path="../lib/node-uuid.d.ts"/>

interface UUIDGenerator {
  random(): string
}

class NodeUUIDGenerator implements UUIDGenerator {

  constructor() {}

  random(): string {
    return uuid.v4();
  }
}

module services {

  export var idGen: UUIDGenerator = new NodeUUIDGenerator();
}

angular.module('righttrack.services', ['righttrack.filters'])
  .value('version', "0.1")
  .factory('$idGen', () => services.idGen)
;
