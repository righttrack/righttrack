/// <reference path="../../lib/angular.d.ts" />
/// <reference path="../util/routes.ts" />
/// <reference path="controllers.ts" />

module tasks {
  export class TasksRoutes extends RouteBuilder {

    public taskList: Route = new Route("/tasks", tasks.controllers.listTasks, "partials/tasklist.html");
  }

  export var routes = new TasksRoutes();
}
