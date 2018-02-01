package io.github.aweiland.oauth4j.provider

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import io.github.aweiland.oauth4j.support.OAuth2Info
import spock.lang.Specification
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import static com.github.tomakehurst.wiremock.client.WireMock.*


abstract class OAuth2ProviderBase<P extends OAuth2Provider> extends Specification {
    
    WireMockServer wireMockServer = new WireMockServer(options().dynamicPort())
    WireMock wireMock = new WireMock("localhost", wireMockServer.port)

    abstract P createProvider()

    def "test provider is configured"() {
        setup:
        def provider = createProvider()

        expect:
        provider?.appSecret?.trim()
        provider?.appId?.trim()
    }

//    abstract MockServerClient createMockServerForToken()
    
    def setup() {
        wireMockServer.start()
    }
    
    def cleanup() {
        wireMockServer.stop()
    }

    // TODO Move this to abstract functions
    def "Test got access token"() {
        given:
        def provider = createProvider()
        provider.setAccessTokenUri("http://localhost:${wireMockServer.port}/token")
        
        and:
        // Not mockserver, wiremock instead!
        wireMock.register(post(urlEqualTo("/token"))
                .willReturn(okJson("jsonstring here"))
        )
        
        // http://www.baeldung.com/mockserver
//        def mockServer = new MockServerClient("127.0.0.1", 1080)
//            .when(request()
//                    .withMethod("POST")
//                    .withPath("/token")
//                     .withBody(exact("{username: 'foo', password: 'bar'}")))
//            .respond(response()
//                .withStatusCode(HttpStatusCode.OK_200.code())
//                .withHeaders(
//                    header(CONTENT_TYPE.toString(), MediaType.JSON.toString())
//                )
//                .withBody("""
//{
//  "access_token": "1234567890-asfsaf",
//  "token_type": "Bearer",
//  "expires_in":  100
//}
//                """.stripIndent()))
                
        
        //def http = Mock(HTTPBuilder)
        //provider.createHttpClient(_) >> http

        when:
        def token = provider.getAccessTokenAndDetails("code")

        then:
        token.present
//        1 * http.post(_ as Map, _ as Closure) >> new OAuth2Info()
    }
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
