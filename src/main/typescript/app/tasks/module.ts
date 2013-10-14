/// <reference path="../../lib/angular.d.ts" />
/// <reference path="../util/modules.ts" />
/// <reference path="routes.ts" />

module tasks {
  export class TasksModule extends Module {

    constructor() {
      super("righttrack.tasks", [], ($routeProvider: ng.IRouteProviderProvider) => {
        tasks.routes.addTo($routeProvider);
      });
    }
  }

  export var module = new TasksModule();
}

