/// <reference path="../../lib/angular.d.ts" />

class Module {
  public module: ng.IModule;

  constructor(public name: string, requires?: string[], configFunction?: Function) {
    this.module = angular.module(name, requires, configFunction);
  }

  bootstrap(elem: HTMLElement): void {
    angular.bootstrap(elem, [this.module]);
  }
}
