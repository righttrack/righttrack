///<reference path="../../lib/angular.d.ts"/>
///<reference path="_all.ts"/>

class Filter {
  constructor(public name: string, public factory: Function) {}
}

module tasks {
  export module filters {
    export var interpolate = new Filter("interpolate",
      (version: string) =>
        (text: any) => String(text).replace(/%VERSION%/mg, version)
    );

    tasks.module.module
      .filter(interpolate.name, interpolate.factory);
  }
}
