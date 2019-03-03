package com.github.aweiland.oauth4j.provider.oauth2

import com.github.aweiland.oauth4j.provider.OAuth2ProviderBase
import com.github.aweiland.oauth4j.provider.flow.AuthVerify
import com.github.aweiland.oauth4j.provider.flow.StartRequest
import com.github.aweiland.oauth4j.support.OAuth2Info

import static com.github.tomakehurst.wiremock.client.WireMock.*


class RedditProviderSpec extends OAuth2ProviderBase<RedditProvider> {

    final CLIENT_ID = "dsafwgqsdfasfasf"
    final CLIENT_SECRET = "985835t3-sdfasfads"
    final AUTH_URI = "https://www.reddit.com/api/v1/authorize"
    final TOKEN_URI = "https://www.reddit.com/api/v1/access_token"
    final API_URI = "https://www.reddit.com/api/v1/me"

    final FINISH_URI = "http://site.com/reddit/finish"

    @Override
    RedditProvider createProvider() {
        def provider = new RedditProvider(CLIENT_ID, CLIENT_SECRET)
        return provider
    }

    

    def "Test get Redirect URI from start"() {
        given: "A Reddit provider"
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
            redirectUri == "${authUri}?client_id=${CLIENT_ID}&redirect_uri=${encodedFinish}&response_type=code&scope=identity"
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
            //.withQueryParam("scope", equalTo("wl.basick"))
            .willReturn(okJson("""{
  "access_token": "1234567890-asfsaf",
  "token_type": "Bearer",
  "expires_in":  100
}""".stripIndent())))

        when: "A token request is sent with a code"
        def verify = provider.verify(r)

        then: "The token exists"
        verify.present
        
        and: "Token data is correct"
        with (verify.get()) {
            token == "1234567890-asfsaf"
            expiresIn == 100
            tokenType == "Bearer"
        }
    }


    def "Test get Reddit details"() {
        given:
        def provider = createProvider()
        provider.setApiUri("http://localhost:${wireMockServer.port()}/api")

        and: "A valid token"
        def token = new OAuth2Info.Builder()
            .provider("reddit")
            .tokenType("Bearer")
            .accessToken("asfasdfsadfa")
            .build()

        and:
        // TODO Can we validate bearer in auth header?
        wireMock.register(get(urlPathEqualTo("/api"))
        .willReturn(okJson("""{
	"kind": "t2", 
	"data": {
		"has_mail": false, 
		"name": "fooBar", 
		"created": 123456789.0, 
		"modhash": "f0f0f0f0f0f0f0f0...", 
		"created_utc": 1315269998.0, 
		"link_karma": 31, 
		"comment_karma": 557, 
		"is_gold": false, 
		"is_mod": false, 
		"has_verified_email": false, 
		"id": "5sryd", 
		"has_mod_mail": false
	}
}""".stripIndent())))

        when:
        def details = provider.getDetails(token)

        then:
        details.present
        with(details.get()) {
            delegate.provider == "reddit"
            providerId == "5sryd"
            displayName == "fooBar"
        }
    }
}
