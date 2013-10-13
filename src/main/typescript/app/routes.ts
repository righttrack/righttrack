/// <reference path="../lib/angular.d.ts" />
/// <reference path="controllers.ts" />

module routes {
  var all: Route[] = [];

  export class Route {
    public path: string;
    public route: ng.IRoute;
    public controller: Function;
    public templateUrl: String;

    constructor(path: string, controller: Function, template: string);
    constructor(path: string, route: ng.IRoute);
    constructor(path: string, route: any, template?: string) {
      this.path = path;
      if (angular.isFunction(route)) {
        var controller = route;
        route.templateUrl = template;
        route.controller = controller;
        this.route = route;
      }
      else if(angular.isObject(route)) {
        this.route = route;
      }
      else {
        throw new TypeError("string | ng.IRoute");
      }
      this.controller = this.route.controller;
      this.templateUrl = this.route.templateUrl;
    }
  }

  module main {

    export var tasks = new Route("/tasks", controllers.listTasks, "partials/tasklist.html");
  }

  export function addTo(provider: ng.IRouteProviderProvider, scope: Object = this) {
    for (var i = 0; i < all.length; i++) {
      var config: Route = all[i];
      provider.when(config.path, config.route)
    }
  }
}
