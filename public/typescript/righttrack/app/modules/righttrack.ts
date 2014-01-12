/// <reference path="../reference.ts" />

module modules {

  export var righttrack: ng.IModule = angular.module("righttrack", ['common', 'ngRoute'])
    .config(($routeProvider: ng.route.IRouteProvider) => {
      $routeProvider
        .when("/tasks", {controller: controllers.TaskListController, template: tasklist.html})
        .otherwise({redirectTo: "/tasks"})
    })
    .controller(controllers)
  ;

}