/// <reference path="../reference.ts" />

module directives {

  export var appVersion = (version: string) => {
    return {
      restrict: 'AE',
      link: ($scope, element: ng.IRootElementService) => {
        element.text(version)
      },
    }
  }

  export interface CurrentTimeScope extends ng.IScope {
    format?: string
    currentTime?: Date
    interval?: number
    stop: () => void
  }

  export var currentTime = ($interval: ng.IIntervalService, dateFilter: (date: Date, format: string) => string) => {
    return {
      restrict: 'AE',
      link: (scope: CurrentTimeScope, element: ng.IRootElementService, attrs: ng.IAttributes) => {
        var promiseToUpdate: ng.IPromise<void> = null

        function updateCurrentTime(): void {
          scope.currentTime = new Date()
        }

        function updateInterval(interval: any): void {
          scope.interval = parseFloat(interval)
          // cancel the previous timeoutId
          scope.stop()
          // save the timeoutId for canceling
          promiseToUpdate = $interval(() => {
            updateCurrentTime() // update DOM
          }, scope.interval)
        }

        scope.stop = () => {
          if (promiseToUpdate != null) {
            $interval.cancel(promiseToUpdate)
            promiseToUpdate = null
          }
        }

        scope.$watch('currentTime', () => {
          element.text(dateFilter(scope.currentTime, scope.format))
        })

        attrs.$observe("format", (value?) => {
          scope.format = value
          updateCurrentTime()
        })

        attrs.$observe("interval", (value?) => {
          updateInterval(value)
        })

        element.on('$destroy', function() {
          scope.stop()
        })

        updateInterval(attrs['interval'] || 1000)
      }
    }
  }
}
