 // <reference path="../reference.ts" />

interface AccessUrl {

  redirectUrl: string

  state: string
}

enum AccountType {
  github
}

class OAuthService {

  constructor(private $http: ng.IHttpService) {}

  authenticate(code: string, state: string): void {
    alert(code)
  }

  fetchAccessUrl(userId: string, account: AccountType): ng.IPromise<AccessUrl> {
    return this.$http.get("/api/user/" + userId + "/link/" + AccountType[account]).then((result) => {
      return result.data
    })
  }
}