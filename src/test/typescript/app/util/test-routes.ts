/// <reference path="../reference.ts" />

import src = require("./app")

describe("RouteTree", () => {

  it("should print tree", () => {

    var tree = new RouteTree("ns", []);
    expect(tree).toEqual("tree");

  });

});
