define ['angular', './models'], (angular) ->
  angular.module('righttrack.services', [])

    .value('version', "0.1")

  x = new User("a", "b", "c")
