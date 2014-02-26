/// <reference path="../reference.ts" />

class Task extends Entity {
  constructor(
    id: string,
    public description: string,
    public completed: boolean = false
  ) {
    super(id)
  }
}
