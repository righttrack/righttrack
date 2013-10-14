/// <reference path="../../lib/angular.d.ts" />

class Route {
  public path: string;
  public route: ng.IRoute;
  public controller: Function;
  public templateUrl: string;

  constructor(path: string, controller: Function, templateUrl: string);
  constructor(path: string, route: ng.IRoute);
  constructor(path: string, route: any, templateUrl?: string) {
    this.path = path;
    if (angular.isFunction(route)) {
      this.route = {
        controller: route,
        templateUrl: templateUrl
      }
    }
    else if(angular.isObject(route)) {
      this.route = route;
    }
    else {
      throw new TypeError("'route' argument must be 'string' or 'ng.IRoute'");
    }
    if (angular.isFunction(this.route.controller)) {
      this.controller = this.route.controller;
    }
    else {
      throw new TypeError("'route.controller' must be 'Function'")
    }
    this.templateUrl = this.route.templateUrl;
  }

  public toString(): string {
    return "Route(" + this.path + " -> " + this.controller + ")";
  }
}

/**
 * Adds static typed route definitions to a common interface.
 *
 * Checks for duplicates and insures the correct signatures for route definitions.
 */
class RouteBuilder {
  /**
   * A fallback route.
   */
  private _fallback: string;

  /**
   * Routes in sequence.
   */
  private _routes: Route[];

  /**
   * Constructs the routes by searching all the properties of the inheriting class for Route declarations.
   */
  constructor() {
    for (var name in this) {
      if (this.hasOwnProperty(name) && this[name] instanceof Route) {
        var routeByName: Route = this[name];
        this._routes.push(routeByName);
      }
    }
  }

  fallbackTo(path: string);
  fallbackTo(route: Route);
  fallbackTo(route: any): RouteBuilder {
    if (angular.isString(route)) {
      this._fallback = route;
    }
    else if (route instanceof Route) {
      this._fallback = route.path;
    }
    else {
      throw new TypeError("'route' must be 'string' or 'Route'");
    }
    return this;
  }

  addTo(provider: ng.IRouteProviderProvider): RouteBuilder {
    for (var i = 0; i < this._routes.length; i++) {
      var config: Route = this._routes[i];
      provider.when(config.path, config.route)
    }
    if (angular.isDefined(this._fallback)) {
      provider.otherwise(this._fallback)
    }
    return this;
  }
}