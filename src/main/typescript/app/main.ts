///<reference path="../lib/angular.d.ts"/>
///<reference path="util/modules.ts"/>
///<reference path="tasks/_all.ts"/>

class MainModule extends Module {

  constructor() {
    super('righttrack', ['ngRoute', tasks.module.name]);
  }
}
