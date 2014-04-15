/// <reference path="../reference.ts" />

module modules {

  export module clients {

    export var api = angular.module("apiClients", [])
      .service("userClient", UserClient)
  }
}