/// <reference path="../reference.ts" />

describe("RouteTree", () => {

  it("should print a tree", () => {
    var result: RouteTree.Branch = RouteTree.from({
      a: new Route("/a", {}),
      b: new Route("/b", {}),
      "1": new Routes({
        c: new Route("/c", {}),
        "2": new Routes({
          d: new Route("/d", {}),
        })
      })
    })
    expect(result.toString()).toBe(
      "Branch([root], [Leaf(a, /a), Leaf(b, /b), Branch(1, [Leaf(1.c, /c), Branch(1.2, [Leaf(1.2.d, /d)])])])"
    )
  })

})

module test {

  export module routes {

    export class StaticRoutesConfig {
      a = new Route("/a", new RouteConfig(() => "a", "a.html"))
      b = new Route("/b", new RouteConfig(() => "b", "b.html"))
      nested = {
        c: new Route("/c", new RouteConfig(() => "c", "c.html")),
        further: {
          d: new Route("/d", new RouteConfig(() => "d", "d.html"))
        },
      }
    }
    export var example = new Routes<StaticRoutesConfig>(new StaticRoutesConfig())

  }
}


describe("Routes", () => {

  it("should allow access to the path", () => {
    var routes = test.routes.example.routes
    expect(routes.a.path).toBe("/a")
    expect(routes.b.path).toBe("/b")
    expect(routes.nested.c.path).toBe("/c")
    expect(routes.nested.further.d.path).toBe("/d")
  })

  it("should add routes to the route provider", () => {
    angular.module("test-routes", ["ngRoute"])
      .config(($routeProvider: ng.route.IRouteProvider) => {
        test.routes.example.appendTo($routeProvider)
      })
    angular.mock.module("test-routes")
    angular.mock.inject(($route: ng.route.IRouteService) => {
      expect($route["/a"]).toBeDefined()
      expect($route["/b"]).toBeDefined()
      expect($route["/c"]).toBeDefined()
    })
  })

})
