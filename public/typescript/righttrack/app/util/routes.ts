/// <reference path="../reference.ts" />

module ClassHelpers {
  var RE_CLASS_NAME = /\W*function\s+([\w\$]+)\(/

  /**
   * Extract the class name from the constructor function.
   *
   * @param obj an instance of an object
   * @returns the name of the class that constructed the object
   */
  export function classNameOf(obj: any): string {
    return RE_CLASS_NAME.exec(obj.constructor.toString())[1]
  }
}

/**
 * The root Route for all route helpers.
 */
class Route {

  /**
   * Build a route for Angular's Ng Route plugin.
   *
   * @param path The url to match
   * @param config The Angular Route config object
   */
  constructor(public path: string, public config: ng.route.IRoute) {}

  toString(): string {
    return this.path
  }
}

/**
 * A Route config object that retains the type information for the controller.
 *
 * These configurations are treated as immutable. Instead of modifying a variable,
 * use the copy method to construct new instances of the config with all the
 * original values (whenever it is passed undefined) and any optional argument.
 */
class RouteConfig<T> implements ng.route.IRoute {

  /**
   * The name of the RouteConfig. This is resolved by the containing Routes
   * object and will be undefined unless this configuration has been initialized.
   */
  name: string

  constructor(
    public controller: T,
    public template: string,
    public resolve?: any
  ) {}

  copy(): RouteConfig<T>
  copy(config: ng.route.IRoute): RouteConfig<any>
  copy(config?: ng.route.IRoute) {
    var copy
    if (!config) {
      copy = new RouteConfig<T>(this.controller, this.template, this.resolve)
      copy.name = this.name
    }
    else {
      copy = new RouteConfig<any>(
        config.controller || this.controller,
        config.template || this.template,
        config.resolve || this.resolve
      )
      copy.name = config.name || this.name
    }
    return copy
  }
}

/**
 * A helper class to retain the controller type and encourage using a typesafe
 * RouteConfig object.
 */
class ControllerRoute<T> {

  constructor(public path: string, public config: RouteConfig<T>) {}
}

module RouteTree {

  export interface Node {
    namespace: string
  }

  export class Leaf implements Node {
    name: string
    constructor(public namespace: string, public route: Route) {}

    toString(): string {
      return ClassHelpers.classNameOf(this) + "(" + this.namespace + ", " + this.route.toString() + ")"
    }
  }

  export class Branch implements Node {
    private _leaves: Leaf[]
    private _subtrees: Branch[]

    constructor(public namespace: string, children: Node[]) {
      this._leaves = []
      this._subtrees = []
      children.forEach((child) => {
        if (child instanceof Branch) {
          this._subtrees.push(<Branch> child)
        }
        else if (child instanceof Leaf) {
          this._leaves.push(<Leaf> child)
        }
        else {
          throw "Unexpected child type: " + ClassHelpers.classNameOf(child)
        }
      })
    }

    isRoot(): boolean {
      return !!this.namespace
    }

    children(): Node[] {
      return (<Node[]> this._leaves).concat(this._subtrees)
    }

    leaves(): Leaf[] {
      return this._leaves
    }

    subtrees(): Branch[] {
      return this._subtrees
    }

    toString(): string {
      return ClassHelpers.classNameOf(this) + "(" + (this.namespace || "[root]") + ", [" +
        this.children().map((child) => child.toString()).join(", ") +
      "])"
    }
  }

  export interface RouteKey {
    namespace: string
    path: string
  }

  export function from(container: any, namespace?: string, knownKeys?: RouteKey[]): Branch {
    knownKeys = knownKeys || []

    // if the namespace is undefined, then use the id function, otherwise prepend the namespace
    var getNamespace: (prop: string) => string = !namespace
      ? (prop) => prop
      : (prop) => namespace + "." + prop

    // convert all children first to traverse in a breadth-first manner
    var props = Object.keys(container)
    var children: Leaf[] = props
      .filter((prop) => container[prop] instanceof Route)
      .map((prop) => new Leaf(getNamespace(prop), container[prop]))

    // verify that there are no duplicate paths
    var childKeys: RouteKey[] = children.map((child) => {
      return {
        namespace: child.namespace,
        path: child.route.path,
      }
    })
    var excludedPaths = knownKeys.map((key: RouteKey) => key.path).sort()
    childKeys.forEach((key) => {
      var duplicateIndex = _.indexOf(excludedPaths, key.path, true)
      if (duplicateIndex >= 0) {
        var thisNamespace = namespace + key.namespace
        var otherNamespace = (<RouteKey> knownKeys[duplicateIndex]).namespace
        throw "Duplicate path found '" + key.path + "' in namespaces '" + thisNamespace + "' and '" + otherNamespace + "'"
      }
    })

    // recursively convert all subtrees
    var subtrees: Branch[] = props
      .map((prop) => {
        var sub = container[prop]
        if (sub instanceof Routes) {
          return {name: prop, children: sub.routes}
        }
        else if (sub instanceof Object && !(sub instanceof Route)) {
          return {name: prop, children: sub}
        }
        else {
          return null
        }
      })
      .filter((config) => config !== null)
      .map((config: {name: string; children: any}) => {
        return RouteTree.from(config.children, getNamespace(config.name), knownKeys.concat(childKeys))
      })

    // combine the subtrees into a final result
    return new Branch(namespace || "", (<Node[]> children).concat(subtrees))
  }
}

/**
 * A Routes container. Inherit from this to get a typed container for storing route variables.
 *
 * All properties of the inheriting class that are of type Route or Routes will be added to this
 * container's RouteTree.
 */
class Routes<T extends Object> {
  /**
   * An object to statically access any routes.
   */
  routes: T
  private _tree: RouteTree.Branch = null;

  constructor(scope: T) {
    this.routes = scope
  }

  tree(): RouteTree.Branch {
    return this._tree
  }

  reload(): void {
    this._tree = RouteTree.from(this.routes)
  }

  children(): Route[] {
    if (this._tree === null) this.reload()
    return this._tree.leaves().map((leaf) => leaf.route)
  }

  subtrees(): Routes<any>[] {
    if (this._tree === null) this.reload()
    return this._tree.subtrees().map((branch) => {
      var routable = {}
      branch.leaves().forEach((leaf) => {
        routable[leaf.name] = leaf.route
      })
      return new Routes(routable)
    })
  }

  appendTo(provider: ng.route.IRouteProvider): void {

  }

}