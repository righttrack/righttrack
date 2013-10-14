///<reference path="../../lib/angular.d.ts"/>
///<reference path="../common/services.ts"/>
///<reference path="models.ts"/>
///<reference path="services.ts"/>

module tasks {
  export module controllers {
    export function listTasks($scope, $id: UUIDGenerator) {
      $scope.showInactive = false;
      $scope.tasks = [
        new Task($id.random(), "Do laundry", false),
        new Task($id.random(), "Make bed", false)
      ];
      $scope.createTask = function(taskForm) {
        var task: Task = new Task($id.random(), taskForm.description, false);
        $scope.tasks.push(task);
        return taskForm.description = "";
      };
      $scope.filterVisible = (task) =>
        task.completed === false || $scope.showInactive;
    }
  }
}