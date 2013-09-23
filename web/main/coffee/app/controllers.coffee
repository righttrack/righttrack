define ['./models'], ->

  tasks: ($scope, $idGen) ->
    $scope.showInactive = false

    $scope.tasks = [
      new Task($idGen.random(), "Do laundry")
      new Task($idGen.random(), "Make bed")
    ]

    $scope.createTask = (taskForm) ->
      task = new Task($idGen.random(), taskForm.description)
      $scope.tasks.push(task)
      taskForm.description = ""

    $scope.filterVisible = (task) ->
      task.completed is false or $scope.showInactive

  view1: ($scope) ->
    $scope.disabled = {
      habits: false,
      rewards: true,
      routines: true,
      todos: false
    }
    $scope.habits = [
      summary: "Clean your dishes after using them"
      bad: false
    ,
      summary: "Make your bed"
      bad: false
    ,
      summary: "Eat sugary food"
      bad: true
    ]
    $scope.rewards = [
      summary: "A new pair of sunglasses",  # TODO: Random free reward
      cost: 0
    ]
    $scope.routines = [
      summary: "Check in to habit RPG",
      frequency: "daily"  # TODO: Make this more like a calendar event
    ]
    $scope.todos = [
      summary: "Click on this box"
    ]

  todo: ($scope) ->


