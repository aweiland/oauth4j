package io.github.aweiland.oauth4j.provider.oauth2

import io.github.aweiland.oauth4j.provider.OAuth2ProviderBase
import io.github.aweiland.oauth4j.provider.flow.AuthVerify
import io.github.aweiland.oauth4j.provider.flow.StartRequest
import io.github.aweiland.oauth4j.support.OAuth2Info

import static com.github.tomakehurst.wiremock.client.WireMock.*
//import static com.github.tomakehurst.wiremock.matching

class GoogleProviderSpec extends OAuth2ProviderBase<GoogleProvider> {

    final CLIENT_ID = "dsafwgqsdfasfasf"
    final CLIENT_SECRET = "985835t3-sdfasfads"
    final AUTH_URI = "https://graph.facebook.com/v2.3/oauth/authorize"
    final TOKEN_URI = "https://graph.facebook.com/v2.3/oauth/access_token"
    final API_URI = "https://graph.facebook.com/v2.3"

    final FINISH_URI = "http://site.com/google/finish"

    @Override
    GoogleProvider createProvider() {
        def provider = new GoogleProvider(CLIENT_ID, CLIENT_SECRET)
        return provider
    }

    

    def "Test get Redirect URI from start"() {
        given: "A Google provider"
        def provider = createProvider()
        def req = new StartRequest.Builder().appId(CLIENT_ID).appSecret(CLIENT_SECRET).scopes(Optional.of("test")).returnUri(FINISH_URI).build()
        def encodedFinish = URLEncoder.encode(FINISH_URI, "UTF-8")
        def authUri = provider.authUri

        when: "A start request is created"
        def red = provider.start(req)

        then: "The request is generated"
        red.present


        and: "The redirect uri is not null and is correct"
        with(red.get()) {
            redirectUri != null
            redirectUri == "${authUri}?client_id=${CLIENT_ID}&redirect_uri=${encodedFinish}&response_type=code&scope=test"
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
  "refresh_token": "refresh-09873547243",
  "token_type": "Bearer",
  "expires_in":  100
}""".stripIndent())))

        when: "A token request is sent with a code"
        def verify = provider.verify(r)

        then: "The token exists"
        verify.present
        
        and: "The token data is valid"
        with (verify.get()) {
            token == "1234567890-asfsaf"
            refreshToken == "refresh-09873547243"
            expiresIn == 100
            tokenType == "Bearer"
        }
    }


    def "Test get Google details"() {
        given:
        def provider = createProvider()
        provider.setApiUri("http://localhost:${wireMockServer.port()}/api")

        and: "A valid token"
        def token = new OAuth2Info.Builder()
            .provider("google")
            .tokenType("Bearer")
            .accessToken("asfasdfsadfa")
            .refreshToken("afsdfasf")
            .build()

        // TODO require Bearer auth header
        and:
        wireMock.register(get(urlPathEqualTo("/api"))
        .withHeader("Authorization", matching("Bearer asfasdfsadfa"))
        .willReturn(okJson("""{
  "id": "goog-98776",
  "displayName": "Timmy"
}""".stripIndent())))

        when:
        def details = provider.getDetails(token)

        then:
        details.present
        with(details.get()) {
            provider == "google"
            providerId == "goog-98776"
            displayName == "Timmy"
        }
    }
    
    
    def "Test get Google details with invalid token"() {
        given:
        def provider = createProvider()
        provider.setApiUri("http://localhost:${wireMockServer.port()}/api")

        and: "A valid token"
        def token = new OAuth2Info.Builder()
            .provider("google")
            .tokenType("Bearer")
            .accessToken("asfasdfsadfa")
            .refreshToken("afsdfasf")
            .build()

        // TODO require Bearer auth header
        and:
        wireMock.register(get(urlPathEqualTo("/api"))
            .willReturn(unauthorized()));

        when:
        def details = provider.getDetails(token)

        then:
        !details.present
    }
}
