/// <reference path="../reference.ts" />

module modules {

  export var main = angular.module('main', [
      services.common.name,
      clients.api.name,
      'ngRoute',
    ])
    .config(($routeProvider: ng.route.IRouteProvider) => {
      $routeProvider
        .when("/home", {template: home.html})
        .when("/tasks", {controller: controllers.TaskListCtrl, template: tasklist.html})
        .when("/authorize/github/oauth", {controller: controllers.GithubOAuthCtrl, template: home.html})
        .otherwise({redirectTo: "/home"})
    })
    .constant("version", "0.1.0")
    .controller(controllers)
    .directive(directives)

}
