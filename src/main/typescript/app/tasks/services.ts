/// <reference path="../common/services.ts" />
/// <reference path="module.ts" />

module tasks {
  export module services {
    tasks.module.module
      .value('version', "0.1")
      .factory('$id', () => common.services.idGenerator)
  }
}
