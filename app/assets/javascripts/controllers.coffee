'use strict'

define ->

  # Controllers
  controllers = {}

  controllers.Board = ($scope) ->
    # TODO: Hook this up to the API
    $scope.disabled = {
      habits: false,
      rewards: true,
      routines: true,
      todos: false
    }
    $scope.habits = [
      {
        summary: "Clean your dishes after using them",
        bad: false
      },
      {
        summary: "Make your bed",
        bad: false
      },
      {
        summary: "Eat sugary food",
        bad: true
      }
    ]
    $scope.rewards = [
      {
        summary: "A new pair of sunglasses",  # TODO: Random free reward
        cost: 0
      }
    ]
    $scope.routines = [
      {
        summary: "Check in to habit RPG",
        frequency: "daily"  # TODO: Make this more like a calendar event
      }
    ]
    $scope.todos = [
      {
        summary: "Click on this box"
      }
    ]

  controllers
