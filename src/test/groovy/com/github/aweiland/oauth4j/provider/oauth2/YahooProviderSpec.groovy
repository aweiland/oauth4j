package com.github.aweiland.oauth4j.provider.oauth2

import com.github.aweiland.oauth4j.provider.OAuth2ProviderBase
import com.github.aweiland.oauth4j.provider.flow.AuthVerify
import com.github.aweiland.oauth4j.provider.flow.StartRequest
import com.github.aweiland.oauth4j.support.OAuth2Info

import static com.github.tomakehurst.wiremock.client.WireMock.*


class YahooProviderSpec extends OAuth2ProviderBase<YahooProvider> {

    final CLIENT_ID = "dsafwgqsdfasfasf"
    final CLIENT_SECRET = "985835t3-sdfasfads"
    final AUTH_URI = "https://api.login.yahoo.com/oauth2/request_auth"
    final TOKEN_URI = "https://api.login.yahoo.com/oauth2/get_token"
    final API_URI = "https://social.yahooapis.com/v1/user/me/profile/tinyusercard"

    final FINISH_URI = "http://site.com/yahoo/finish"

    @Override
    YahooProvider createProvider() {
        def provider = new YahooProvider(CLIENT_ID, CLIENT_SECRET)
        return provider
    }

    

    def "Test get Redirect URI from start"() {
        given: "A Yahoo provider"
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
            redirectUri == "${authUri}?client_id=${CLIENT_ID}&redirect_uri=${encodedFinish}&response_type=code"
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
  "refresh_token": "AOiRUlJn_qOmByVGTmUpwcMKW3XDcipToOoHx2wRoyLgJC_RFlA-",
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
            refreshToken == "AOiRUlJn_qOmByVGTmUpwcMKW3XDcipToOoHx2wRoyLgJC_RFlA-"
        }
    }


    def "Test get Yahoo details"() {
        given:
        def provider = createProvider()
        provider.setApiUri("http://localhost:${wireMockServer.port()}/api")

        and: "A valid token"
        def token = new OAuth2Info.Builder()
            .provider("yahoo")
            .tokenType("Bearer")
            .accessToken("asfasdfsadfa")
            .build()

        and:
        // TODO Can we validate bearer in auth header?
        wireMock.register(get(urlPathEqualTo("/api"))
            .willReturn(okJson("""{
	"profile": {
		"guid": "yahoo-123456",
		"nickname": "Timmy",
		"image": {
			"size": "string",
			"width": 100,
			"height": 100,
			"imageUrl": "string"
		},
		"gender": "string",
		"profileUrl": "string",
		"uri": "string"
	}
}""".stripIndent())))

        when:
        def details = provider.getDetails(token)

        then:
        details.present
        with(details.get()) {
            delegate.provider == "yahoo"
            providerId == "yahoo-123456"
            displayName == "Timmy"
        }
    }
}
