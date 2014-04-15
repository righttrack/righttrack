/// <reference path="../reference.ts" />

module controllers {

  interface CreateUserForm {
    username: string
    firstName: string
    lastName: string
    email: string
  }

  export class CreateUserCtrl {

    constructor($scope, idGen: EntityIdGenerator, userClient: UserClient) {

      $scope.createUser = (form: CreateUserForm) => {
        var user = new User(idGen.next(), form.username, {first: form.firstName, last: form.lastName}, new Email(form.email))
        userClient.putUser(user).then((result) => {
          $scope.result = result
        }, (fail) => {
          console.error("Could not contact host", fail)
        })
      }

      $scope.reset = () => $scope.user = {}
    }
  }
}