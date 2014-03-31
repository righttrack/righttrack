// <reference path="../reference.ts" />

describe("GithubOAuthController", () => {

  beforeEach(angular.mock.module(modules.main.name))

  it("should parse the state and access code parameters in the url", () => {
    var code = "authorization_code"
    var state = "testing"
    var oauthService: any = {
      authenticate: () => {}
    }
    spyOn(oauthService, "authenticate")
    var window: any = {
      location: {
        search: "?code=" + code + "&state=" + state
      }
    }
    new controllers.GithubOAuthController(window, oauthService)
    expect(oauthService.authenticate).toHaveBeenCalledWith(code, state)
  })

})
