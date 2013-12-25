/// <reference path="reference.ts" />

"use strict";

angular.module('righttrack', ['ngRoute', 'controllers', 'services'])
  .config(($routeProvider: ng.route.IRouteProvider) => {
    // TODO: Move to routes file
    $routeProvider.when("/tasks", {controller: controllers.TaskListController, template: tasklist.html});
  });

angular.bootstrap(document.body, ['righttrack']);
