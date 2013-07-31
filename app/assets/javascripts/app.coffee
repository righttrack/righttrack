require.config
  paths:
    angular: "/webjars/angularjs/1.1.5"
    dijit: "/webjars/dojo/1.9.1/dijit"
    dojo: "/webjars/dojo/1.9.1/dojo"
    dojox: "/webjars/dojo/1.9.1/dojox"

define 'angular', ['angular/angular'], ->
  require ['angular/i18n/angular-locale_en-us']
  angular

require ['angular', './controllers', './directives', './filters', './services'],
  (angular, controllers) ->

    # This is based on the angular seed project setup, but it should probably be split into modules
    # based on views instead of type
    angular.module('righttrack', ['righttrack.directives', 'righttrack.filters', 'righttrack.services'])
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

