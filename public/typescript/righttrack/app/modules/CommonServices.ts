
module modules {

  export module services {

    export var common: ng.IModule = angular.module("common", []).

      service("$uuid", () => new NodeUUIDGenerator())

    ;
  }
}
