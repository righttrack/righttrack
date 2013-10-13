///<reference path="../lib/angular.d.ts"/>
///<reference path="../lib/node-uuid.d.ts"/>

var idGen: UUIDGenerator = new NodeUUIDGenerator();
angular.module('righttrack.services', []).value('version', "0.1").factory('$idGen', () => idGen);

interface UUIDGenerator {
  random(): string
}

class NodeUUIDGenerator implements UUIDGenerator {

  constructor() {}

  random(): string {
    return uuid.v4();
  }
}
