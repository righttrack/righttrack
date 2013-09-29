define(['angular', 'lib/node-uuid'], function(angular, uuid) {
  return angular.module('righttrack.services', []).value('version', "0.1").factory('$idGen', function() {
    return {
      random: function() {
        return uuid.v4();
      }
    };
  });
});
