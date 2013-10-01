
export class Model {}

/*
  User models
*/

export class User extends Model {}

/*
  Task models
*/

export class Task extends Model {
  id: string;
  description: string;
  completed: bool;

  constructor(id: string, description: string, completed?: bool) {
    this.id = id;
    this.description = description;
    this.completed = completed;
  }
}
