/// <reference path="../reference.ts" />

module controllers {

  interface CreateTaskForm {
    description: string;
  }

  export class TaskListCtrl {

    constructor($scope: any, idGen: EntityIdGenerator) {
      $scope.tasklist = {
        name: "Work Project 1",
      }
      $scope.tasks = [
        new Task(idGen.next(), "Task A"),
        new Task(idGen.next(), "Task B"),
      ]
      $scope.createTask = (taskForm: CreateTaskForm) => {
        $scope.tasks.push(new Task(idGen.next(), taskForm.description))
        taskForm.description = ""
      }
      $scope.filterCompleted = (task: Task) => $scope.showCompleted || !task.completed
      $scope.showCompleted = false
    }
  }
}