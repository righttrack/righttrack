///<reference path="../lib/angular.d.ts"/>

angular.module('righttrack.filters', []).filter('interpolate', [
  'version', function(version) {
    return function(text) {
      return String(text).replace(/%VERSION%/mg, version);
    };
  }
]);
