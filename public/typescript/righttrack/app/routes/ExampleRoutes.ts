/// <reference path="../reference.ts" />

class TaskRoutes {

  tasks = new Route("/tasks", {controller: controllers.TaskListController, template: tasklist.html})

}

modules.righttrack.value("routes", new Routes(new TaskRoutes()))
