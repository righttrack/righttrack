/// <reference path="../reference.ts" />

interface Name {
  first: string
  last: string
}

class AuthAccount extends Entity {
}

class User extends Entity {
  constructor(
    id: string,
    public username: string,
    public name: Name,
    public email: Email,
    public auth: AuthAccount[] = []) {
    super(id)
  }
}
