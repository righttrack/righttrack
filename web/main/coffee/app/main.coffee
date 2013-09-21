define ['angular', './controllers', './directives', './filters', './services'],
  (angular, controllers) ->
    console.log "loaded angular", angular

    # This is based on the angular seed project setup, but it should probably be split into modules
    # based on views instead of type
    main = angular.module('righttrack', ['ngRoute', 'righttrack.directives', 'righttrack.filters', 'righttrack.services'])
    main
      .config(($routeProvider) ->
        $routeProvider.when '/view1', templateUrl: 'partials/board.html', controller: controllers.view1
        $routeProvider.when '/todo', templateUrl: 'partials/board.html', controller: controllers.todo
        $routeProvider.otherwise redirectTo: '/view1'
      )
      # Change to main controller with access to $rootScope
      .controller('main', ($scope) ->
        $scope
      )

    angular.bootstrap document, ['righttrack']
