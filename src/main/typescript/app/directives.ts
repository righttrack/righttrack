///<reference path="../lib/angular.d.ts"/>

angular.module("righttrack.directives", []).directive('rtVersion', function(version) {
  return function($scope, elem, attrs) {
    return elem.text(version);
  };
});
