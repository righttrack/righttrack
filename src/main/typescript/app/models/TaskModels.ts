/// <reference path="../reference.ts" />

class Task extends EntityModel {
  constructor(id: string, public description: string) {
    super(id);
  }
}
