package io.github.aweiland.oauth4j.provider

import io.github.aweiland.oauth4j.ProviderRegistry
import io.github.aweiland.oauth4j.provider.flow.StartRequest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import io.github.aweiland.oauth4j.support.OAuth2Info
import spock.lang.Specification
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import static com.github.tomakehurst.wiremock.client.WireMock.*


abstract class OAuth2ProviderBase<P extends OAuth2Provider> extends Specification {
    
    WireMockServer wireMockServer = new WireMockServer(options().dynamicPort())
    WireMock wireMock

    // This is done to call the static initializer that hooks a Jackson Object
    // It's a hack for now.  It works when the full suite is run, but not when
    // tests are run one by one
    def registry = new ProviderRegistry()


    abstract P createProvider()

    def "test provider is configured"() {
        setup:
        def provider = createProvider()

        expect:
        provider?.appSecret?.trim()
        provider?.appId?.trim()
    }


    def setup() {
        wireMockServer.start()
        wireMock = new WireMock("localhost", wireMockServer.port())
    }
    
    def cleanup() {
        wireMockServer.stop()
    }

    // TODO Move this to abstract functions
    def "Test got access token"() {
        given: "A Facebook provider"
        def provider = createProvider()
        provider.setAccessTokenUri("http://localhost:${wireMockServer.port()}/token")
        provider.setApiUri("http://localhost:${wireMockServer.port()}/api")

        def req = new StartRequest.Builder().appId(CLIENT_ID).appSecret(CLIENT_SECRET).returnUri(FINISH_URI).build()
        
        and: "Mocked endpoints"
        // Not mockserver, wiremock instead!
        wireMock.register(post(urlEqualTo("/token"))
                .willReturn(okJson("""{
  "access_token": "1234567890-asfsaf",
  "token_type": "Bearer",
  "expires_in":  100
}""".stripIndent())))

        when: "A token request is sent with a code"
        def token = provider.performCodeExchange("code", req)

        then: "The token exists"
        token.present
        token.get().token == "1234567890-asfsaf"
        token.get().expiresIn == 100
        token.get().tokenType == "Bearer"
    }


//        wireMock.register(get(urlEqualTo("/api"))
//        .willReturn(okJson("""
//{
//  "id": "fb-98776",
//  "name": "Timmy",
//  "first_name":  "Timmy",
//  "last_name": "Alsotimmy"
//}
//                """.stripIndent())))




//
//    def "Test failed access token"() {
//        setup:
//        def provider = createProvider()
//        def http = Mock(HTTPBuilder)
//        provider.createHttpClient(_) >> http
//
//        when:
//        def token = provider.getAccessTokenAndDetails("code")
//
//        then:
//        1 * http.post(_ as Map, _ as Closure) >> { throw new HttpResponseException() }
//        !token.present
//    }
}
