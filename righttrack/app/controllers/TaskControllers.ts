/// <reference path="../reference.ts" />

module controllers {

  export class TaskListController {

    constructor($scope, $uuid: UUIDGenerator) {
      $scope.tasks = [
        new Task($uuid.nextId(), "Task A"),
        new Task($uuid.nextId(), "Task B")
      ];
    }
  }
}