package io.github.aweiland.oauth4j.provider.oauth1

import io.github.aweiland.oauth4j.provider.OAuth1ProviderBase
import io.github.aweiland.oauth4j.provider.flow.AuthVerify
import io.github.aweiland.oauth4j.provider.flow.StartRequest
import io.github.aweiland.oauth4j.support.OAuth1Info

import static com.github.tomakehurst.wiremock.client.WireMock.*


class TwitterProviderSpec extends OAuth1ProviderBase<TwitterProvider> {

    final CLIENT_ID = "dsafwgqsdfasfasf"
    final CLIENT_SECRET = "985835t3-sdfasfads"


    final FINISH_URI = "http://site.com/twitter/finish"

    @Override
    TwitterProvider createProvider() {
        def provider = new TwitterProvider(CLIENT_ID, CLIENT_SECRET)
        return provider
    }

    

    def "Test get Redirect URI from start"() {
        given: "A Twitter provider"
        def provider = createProvider()
        provider.setAuthUri("http://localhost:${wireMockServer.port()}/token")
        def req = new StartRequest.Builder().appId(CLIENT_ID).appSecret(CLIENT_SECRET).returnUri(FINISH_URI).build()


        and: "Mocked endpoints"
        wireMock.register(post(urlEqualTo("/token"))
                .willReturn(ok("oauth_token=NPcudxy0yU5T3tBzho7iCotZ3cnetKwcTIRlX0iwRl0&" +
                "oauth_token_secret=veNRnAWe6inFuo8o2u8SLLZLjolYDmDP7SzL0YfYI&" +
                "oauth_callback_confirmed=true".stripIndent())))



        when: "A start request is created"
        def red = provider.start(req)

        then: "The request is generated"
        red.present

        and: "The redirect uri is not null and is correct"
        with(red.get()) {
            redirectUri != null
            redirectUri == "https://api.twitter.com/oauth/authorize?oauth_token=NPcudxy0yU5T3tBzho7iCotZ3cnetKwcTIRlX0iwRl0"
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
                .requestToken("sdfsfsfeed23425")
                .code("afsfasf")
                .returnUri(FINISH_URI)
                .build()

        and: "Mocked endpoints"
        wireMock.register(post(urlEqualTo("/token"))
                .willReturn(ok("oauth_token=7588892-kagSNqWge8gB1WwE3plnFsJHAZVfxWD7Vb57p0b4&oauth_token_secret=PbKfYqSryyeKDWz4ebtY3o5ogNLG11WJuZBc9fQrQo".stripIndent())))

        when: "A token request is sent with a code"
        def authToken = provider.verify(r)

        then: "The token exists"
        authToken.present

        and:
        with(authToken.get()) {
            token == "7588892-kagSNqWge8gB1WwE3plnFsJHAZVfxWD7Vb57p0b4"
            secretToken == "PbKfYqSryyeKDWz4ebtY3o5ogNLG11WJuZBc9fQrQo"
        }
    }


    def "Test get Twitter details"() {
        given:
        def provider = createProvider()
        provider.setApiUri("http://localhost:${wireMockServer.port()}/")

        and: "A valid token"
        def token = new OAuth1Info.Builder()
            .provider("facebook")
            .token("asfasdf")
            .secret("sfasfsaf")
            .build()

        and:
        wireMock.register(get(urlPathEqualTo("/account/verify_credentials.json"))
        .willReturn(okJson("""{
  "id": 98776,
  "id_str": "98776"
}""".stripIndent())))

        when:
        def details = provider.getDetails(token)

        then:
        details.present
        with(details.get()) {
            providerId == "98776"
        }
    }
}
