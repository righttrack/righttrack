///<reference path="../lib/angular.d.ts"/>
///<reference path="controllers.ts"/>
///<reference path="routes.ts"/>
///<reference path="util/modules.ts"/>

class Main implements NgModWrapper {
  module: ng.IModule;

  constructor() {
    this.module = angular.module('righttrack', ['ngRoute', 'righttrack.directives', 'righttrack.filters', 'righttrack.services']);
    this.module.config(function($routeProvider: ng.IRouteProviderProvider) {
      $routeProvider.when('/tasks', {
        templateUrl: 'partials/tasklist.html',
        controller: controllers.listTasks
      });
      $routeProvider.when('/view1', {
        templateUrl: 'partials/board.html',
        controller: controllers.view1
      });
      $routeProvider.when('/todo', {
        templateUrl: 'partials/board.html',
        controller: controllers.todo
      });
      $routeProvider.otherwise({
        redirectTo: '/tasks'
      });
    });
  }

  public bootstrap(elem: HTMLElement): void {
    angular.bootstrap(elem, ['righttrack']);
  }
}
