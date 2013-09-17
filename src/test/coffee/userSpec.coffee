require ['../../'], (models) ->
  x = new models.User("2", "3", "4")
  alert x.id
