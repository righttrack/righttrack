/// <reference path="../reference.ts" />

class Email extends Model {

  static validate(address: string): boolean {
    return address != ""
  }

  constructor(public address: string) {
    super()
    assert(Email.validate(address), "Invalid format for email address.")
  }

  toJSON(): string {
    return this.address
  }
}
