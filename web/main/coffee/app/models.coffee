# All the models are defined on the window object
define -> this

moduleKeywords = ['extended', 'included']

class Mixable
  ###
    A common definition for mixing methods and instance variables into a class
  ###
  @extend: (obj) ->
    for key, value of obj when key not in moduleKeywords
      @[key] = value

    obj.extended?.apply(@)
    this

  @include: (obj) ->
    for key, value of obj when key not in moduleKeywords
      # Assign properties to the prototype
      @::[key] = value

    obj.included?.apply(@)
    this

class Model extends Mixable

###
  User models
###

class User extends Model
  constructor: (@id, @email, @name) ->

###
  Task models
###

class Task extends Model
  constructor: (@id, @description, @completed = false) ->

