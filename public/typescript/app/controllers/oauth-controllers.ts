// <reference path="../reference.ts" />

module controllers {

  export class GithubOAuthController {

    constructor($window: ng.IWindowService, oauth: OAuthService) {
      var query: any = URI.parseQuery($window.location.search)
      oauth.authenticate(query.code, query.state)
    }
  }
}