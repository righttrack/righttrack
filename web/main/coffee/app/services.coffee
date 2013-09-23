define ['angular', 'lib/node-uuid'], (angular, uuid) ->

  angular.module('righttrack.services', [])

    .value('version', "0.1")

    .factory('$idGen', ->
      random: -> uuid.v4()
    )


