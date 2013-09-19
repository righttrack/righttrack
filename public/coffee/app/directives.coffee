define ['lib/angular'], (angular) ->
  angular.module("righttrack.directives", [])

    .directive('rtVersion', (version) ->
      ($scope, elem, attrs) ->
        elem.text(version)
    )
