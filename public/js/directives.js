(function() {

  define(['angular'], function(angular) {
    return angular.module('myApp.directives', [])
      .directive('appVersion', [
        'version', function (version) {
          return function (scope, elm, attrs) {
            return elm.text(version);
          };
        }
      ])
      .directive('rtEvents', function () {
        return {
          scope: {
            events: "="
          },
          templateUrl: "directives/events.html",
          link: function(scope, elm, attrs) {
            console.log(scope);
            console.log(attrs);
//            scope.events = attrs.events;
          }
        };
      });
  });

}).call(this);
