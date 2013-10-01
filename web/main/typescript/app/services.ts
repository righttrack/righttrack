///<reference path="../lib/angular.d.ts"/>
///<reference path="../lib/node-uuid.d.ts"/>

var idGen: UUIDGenerator = new UUIDGenerator(function () { return uuid.v4(); });
angular.module('righttrack.services', []).value('version', "0.1").factory('$idGen', () => idGen);

export class UUIDGenerator {
  generate: () => String;

  constructor(generate: () => String) {
    this.generate = generate;
  }

  random(): String {
    return this.generate();
  }
}
