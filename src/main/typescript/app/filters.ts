///<reference path="../lib/angular.d.ts"/>

angular.module('righttrack.filters', [])
  .filter('interpolate', (version: string) =>
      (text: any) => String(text).replace(/%VERSION%/mg, version)
  )
;