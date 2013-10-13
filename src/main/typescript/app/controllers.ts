///<reference path="../lib/angular.d.ts"/>
///<reference path="models.ts"/>
///<reference path="services.ts"/>

module controllers {

  export function listTasks($scope, $idGen: UUIDGenerator) {
    $scope.showInactive = false;
    $scope.tasks = [
      new Task($idGen.random(), "Do laundry", false),
      new Task($idGen.random(), "Make bed", false)
    ];
    $scope.createTask = function(taskForm) {
      var task: Task = new Task($idGen.random(), taskForm.description, false);
      $scope.tasks.push(task);
      return taskForm.description = "";
    };
    return $scope.filterVisible = function(task) {
      return task.completed === false || $scope.showInactive;
    };
  }

  export function view1($scope) {
    $scope.disabled = {
      habits: false,
      rewards: true,
      routines: true,
      todos: false
    };
    $scope.habits = [
      {
        summary: "Clean your dishes after using them",
        bad: false
      }, {
        summary: "Make your bed",
        bad: false
      }, {
        summary: "Eat sugary food",
        bad: true
      }
    ];
    $scope.rewards = [
      {
        summary: "A new pair of sunglasses",
        cost: 0
      }
    ];
    $scope.routines = [
      {
        summary: "Check in to habit RPG",
        frequency: "daily"
      }
    ];
    return $scope.todos = [
      {
        summary: "Click on this box"
      }
    ];
  }

  export function todo($scope) {}

}