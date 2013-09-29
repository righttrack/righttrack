define(['angular', './controllers', './directives', './filters', './services'], function(angular, controllers) {
  var main;
  console.log("loaded angular", angular);
  main = angular.module('righttrack', ['ngRoute', 'righttrack.directives', 'righttrack.filters', 'righttrack.services']);
  main.config(function($routeProvider) {
    $routeProvider.when('/tasks', {
      templateUrl: 'partials/tasklist.html',
      controller: controllers.tasks
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
  }).controller('main', function($scope) {
    return $scope;
  });
  return angular.bootstrap(document, ['righttrack']);
});
