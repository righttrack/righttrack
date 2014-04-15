/// <reference path="../reference.ts" />

class UserClient {

  constructor(private $http: ng.IHttpService) {}

  // TODO: Return the correct data type by parsing json and calling a constructor
  putUser(user: User): ng.IHttpPromise<any> {
    return this.$http.put("/api/user", angular.toJson(user))
  }

}