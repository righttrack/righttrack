/// <reference path="../reference.ts" />

/**
 *
 * @param obj an instance of an object
 * @returns the name of the class that constructed the object
 */
//var CLASS_NAME = /\W*function\s+([\w\$]+)\(/;
//function classNameOf(obj: Object): string {
//  return CLASS_NAME.exec(obj.toString())[1];
//}

/**
 * A helpful module for convention over configuration.
 */
//module routes {
//  export var all = new Routes();
//  export var unmatched: ng.IRoute = null;
//
//  export function add(name: string, path: string, config: ng.IRoute): void {
//    all.route(name, path, config);
//  }
//
//  export function otherwise(config: ng.IRoute): void {
//    this.unmatched = config;
//  }
//
//
//}

//TODO: Map a RouteTree

//interface RouteNode {}
//
//interface RouteTree extends RouteNode {
//  [name: string]: RouteNode
//}
//
//interface RouteLeaf extends RouteNode {
//  name: string
//  route: Route
//}
//
//interface RouteMap {
//  [namespace: string]: Route
//}



/**
 * A helpful module for convention over configuration.
 */
//module routes {
//  export var all: Route[] = [];
//}
//
//function Routes<RoutesByName extends RouteMap>(routesByName: RoutesByName): RoutesByName {
//  Object.keys(routesByName).map((key: string) => {
//    var named = routesByName[key];
//    if (named instanceof Route) {
//      routes.all.push(named);
//    }
//    else {
//      Routes(named);
//    }
//  });
//  return routesByName;
//}

/**
 * The root Route for all route helpers.
 */
class Route {
  constructor(public path: string, public config: ng.IRoute) {}
}

interface IRouteModule {
  /**
   * @return Either[]
   */
  [namespace: string]: any
}

class RouteTree {
  constructor(public namespace: string, public children: RouteTree[]) {}

  static build(routeMap: IRouteModule, rootName: string = "root"): RouteTree {
    var namespaces = Object.keys(routeMap);
    var children: RouteTree[] = [];
    namespaces.forEach(
      (key: string) => {
        var namespace = rootName + "." + key;
        var routable = routeMap[key];
        var subtree: RouteTree =
          routable instanceof Route ?
            new RouteTree(namespace, []) :
            RouteTree.build(routeMap[key], namespace) ;
        children.push(subtree);
      }
    );
    return new RouteTree(rootName, children);
  }

  toString(): string {
    return "tree"
  }
}

//interface RouteEntry {
//  namespace: string
//  routes: RouteMap
//}
//
//class RoutesBuilder {
//  routes: RouteMap;
//
//
//
//  constructor(routeMap: RouteTree) {
//    var firstLayer = _.map(routeMap, function(value: RouteNode, key: string) {
//      return {
//        namespace: key,
//
//      };
//    });
//    var clone = _.foldl<RouteEntry, RouteMap>({
//      name:
//      routeMap}, )
//  }
//
//  static addToNamespace(ns: string, routes: RouteNode, to: RouteMap): RouteMap {
//    if (routes instanceof Route) {
//      var route = routes;
//      to[ns] = route;
//    }
//    else {
//      _.forEach(routes, (routable: RouteNode, key: string) => {
//        RoutesBuilder.addToNamespace(ns + '.' + key, routable, to);
//      });
//    }
//    return to;
//  }
//
//  static addAll(routes: RouteTree, to: RouteMap): RouteMap {
//    _.forEach((route: RouteNode, key: string) => {
//      to = RoutesBuilder.addToNamespace(key, route, to);
//    });
//    return to;
//  }
//}
