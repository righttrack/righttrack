/// <reference path="reference.ts" />

class TestEntity extends Entity {
  constructor(id: string, public mutable: TestNestableModel) {
    super(id)
  }
}

class TestNestableModel {
  constructor(public value: string) {}
}

describe("models", () => {

  beforeEach(angular.mock.module(modules.main.name))

  it ("should convert to json like a plain object", inject(() => {
    var x = new TestEntity("id", new TestNestableModel("value"))
    var y = angular.copy(x)
    expect(x).toEqual(y)
  }))

})
