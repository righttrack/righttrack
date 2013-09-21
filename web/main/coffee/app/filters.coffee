define ['angular'], (angular) ->
  angular.module('righttrack.filters', [])

    .filter('interpolate', ['version', (version) ->
      (text) -> String(text).replace(/\%VERSION\%/mg, version)
    ])
