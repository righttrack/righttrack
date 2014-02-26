/// <reference path="../reference.ts" />

class User extends Entity {
  constructor(id: string, public name: string, public email: Email) {
    super(id)
  }
}
