
require(['lib/node-uuid'], function(uuid) {
  var idGen: UUIDGenerator = new UUIDGenerator(uuid.v4);
  angular.module('righttrack.services', []).value('version', "0.1").factory('$idGen', () => idGen);
});

export class UUIDGenerator {
  generate: () => String;

  constructor(generate: () => String) {
    this.generate = generate;
  }

  random(): String {
    return this.generate();
  }
}
