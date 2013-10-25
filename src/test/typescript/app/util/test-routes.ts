/// <reference path="../reference.ts" />

describe("RouteTree", () => {

  it("should print tree", () => {

    var tree = new RouteTree("ns", []);
    expect(tree).toEqual("tree");

  });

});
