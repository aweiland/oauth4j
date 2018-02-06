package io.github.aweiland.oauth4j.provider.oauth2

import io.github.aweiland.oauth4j.provider.OAuth2ProviderBase
import io.github.aweiland.oauth4j.provider.flow.AuthVerify
import io.github.aweiland.oauth4j.provider.flow.StartRequest
import io.github.aweiland.oauth4j.provider.oauth2.FacebookProvider
import io.github.aweiland.oauth4j.support.OAuth2Info

import static com.github.tomakehurst.wiremock.client.WireMock.*


class GithubProviderSpec extends OAuth2ProviderBase<GithubProvider> {

    final CLIENT_ID = "dsafwgqsdfasfasf"
    final CLIENT_SECRET = "985835t3-sdfasfads"
    final AUTH_URI = "https://graph.facebook.com/v2.3/oauth/authorize"
    final TOKEN_URI = "https://graph.facebook.com/v2.3/oauth/access_token"
    final API_URI = "https://graph.facebook.com/v2.3"

    final FINISH_URI = "http://site.com/github/finish"

    @Override
    GithubProvider createProvider() {
        def provider = new GithubProvider(CLIENT_ID, CLIENT_SECRET)
        return provider
    }

    

    def "Test get Redirect URI from start"() {
        given: "A GitHub provider"
        def provider = createProvider()
        def req = new StartRequest.Builder().appId(CLIENT_ID).appSecret(CLIENT_SECRET).returnUri(FINISH_URI).build()
        def encodedFinish = URLEncoder.encode(FINISH_URI, "UTF-8")
        def authUri = provider.authUri

        when: "A start request is created"
        def red = provider.start(req)

        then: "The request is generated"
        red.present
        

        and: "The redirect uri is not null and is correct"
        with (red.get()) {
            redirectUri != null
            redirectUri == "${authUri}?client_id=${CLIENT_ID}&redirect_uri=${encodedFinish}&scope=read%3Auser"
        }
    }


    def "Test got access token"() {
        given: "A Provider"
        def provider = createProvider()
        provider.setAccessTokenUri("http://localhost:${wireMockServer.port()}/token")

        and: "A verify request"
        def r = new AuthVerify.Builder()
                .appId(CLIENT_ID)
                .appSecret(CLIENT_SECRET)
                .code("test")
                .returnUri(FINISH_URI)
                .build()

        and: "Mocked endpoints"
        wireMock.register(post(urlEqualTo("/token"))
                .willReturn(okJson("""{
  "access_token": "1234567890-asfsaf",
  "token_type": "bearer",
  "scope":  "read:user"
}""".stripIndent())))

        when: "A token request is sent with a code"
        def verify = provider.verify(r)

        then: "The token exists"
        verify.present
        
        and: "Token data is correct"
        with (verify.get()) {
            token == "1234567890-asfsaf"
            tokenType == "bearer"
        }
    }


    def "Test get GitHub details"() {
        given:
        def provider = createProvider()
        provider.setApiUri("http://localhost:${wireMockServer.port()}/api")

        and: "A valid token"
        def token = new OAuth2Info.Builder()
            .provider("github")
            .tokenType("bearer")
            .accessToken("asfasdfsadfa")
            .build()

        and:
        wireMock.register(get(urlPathEqualTo("/api"))
        .willReturn(okJson("""{
  "id": "gh-98776",
  "name": "Timmy",
  "login":  "TimmyUser"
}""".stripIndent())))

        when:
        def details = provider.getDetails(token)

        then:
        details.present
        with(details.get()) {
            providerId == "gh-98776"
            displayName == "Timmy"
            firstName == "TimmyUser"
        }
    }
}
