
interface Model {}

/*
  User models
*/

class User implements Model {}

/*
  Task models
*/

class Task implements Model {
  id: string;
  description: string;
  completed: boolean;

  constructor(id: string, description: string, completed: boolean) {
    this.id = id;
    this.description = description;
    this.completed = completed;
  }
}
