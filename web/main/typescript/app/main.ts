///<reference path="../lib/require.d.ts"/>
///<reference path="../lib/angular.d.ts"/>
///<reference path="controllers.ts"/>
///<reference path="util/modules.ts"/>

require(["angular"])

class Main implements NgModWrapper {
  mod: ng.IModule;

  constructor() {
    this.mod = angular.module('righttrack', ['ngRoute', 'righttrack.directives', 'righttrack.filters', 'righttrack.services'], null);
    this.mod.config(function($routeProvider) {
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
      return $routeProvider.otherwise({
        redirectTo: '/tasks'
      });
    });
    this.mod.controller('main', function($scope) {
      return $scope;
    });
  }

  public bootstrap(): void {
    angular.bootstrap(document.body, ['righttrack']);
  }
}