///<reference path="../../lib/angular.d.ts"/>
///<reference path="module.ts"/>

class Directive {
  constructor(public name: string, public factory: Function) {}
}

module tasks {
  export module directives {
    var version = new Directive('rtVersion',
      (version: string) =>
        ($scope, elem, attrs) => elem.text(version)
    );

    tasks.module.module
      .directive(version.name, version.factory)
  }
}

