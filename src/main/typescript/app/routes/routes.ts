/// <reference path="../reference.ts" />

module routes {

  module tasks {

    export var list = new Route("/tasks", {controller: controllers.TaskListController})

    module nested {

      export var slamJam = new Route("/slam", {})
    }
  }
}
