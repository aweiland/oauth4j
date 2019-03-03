package io.github.aweiland.oauth4j.provider

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import io.github.aweiland.oauth4j.ProviderRegistry
import io.github.aweiland.oauth4j.provider.flow.AuthVerify
import io.github.aweiland.oauth4j.provider.flow.StartRequest
import io.github.aweiland.oauth4j.support.OAuth1Info
import io.github.aweiland.oauth4j.support.OAuth2Info
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options

abstract class OAuth1ProviderBase<P extends OAuth1Provider> extends Specification {
    
    WireMockServer wireMockServer = new WireMockServer(options().dynamicPort())
    WireMock wireMock

    // This is done to call the static initializer that hooks a Jackson Object
    // It's a hack for now.  It works when the full suite is run, but not when
    // tests are run one by one
    def registry = new ProviderRegistry()


    abstract P createProvider()


    def setup() {
        wireMockServer.start()
        wireMock = new WireMock("localhost", wireMockServer.port())
    }
    
    def cleanup() {
        wireMockServer.stop()
    }


    def "Test provider is configured"() {
        setup:
        def provider = createProvider()

        expect:
        provider?.appSecret?.trim()
        provider?.appId?.trim()
    }


    def "Test failed access token"() {
        given:
        def provider = createProvider()
        def req = new StartRequest.Builder().appId("asf").appSecret("asf").returnUri("sfadf").build()

        and: "A verify request"
        def r = new AuthVerify.Builder()
                .appId(CLIENT_ID)
                .appSecret(CLIENT_SECRET)
                .code("test")
                .returnUri(FINISH_URI)
                .requestToken("sfsfsf")
                .build()


        and: "Mocked endpoints will fail"
        wireMock.register(post(urlEqualTo("/token"))
                .willReturn(badRequest()))

        when:
        def token = provider.verify(r)

        then:
        !token.present
    }

    def "Test failed getting provider details"() {
        given:
        def provider = createProvider()

        and: "Valid token"
        and: "A valid token"
        def token = new OAuth1Info.Builder()
                .provider("facebook")
                .token("asfasdfsadfa")
                .secret("ssdfsfsfsfsf")
                .build()

        and: "Mocked endpoints will fail"
        wireMock.register(post(urlPathEqualTo("/api"))
                .willReturn(badRequest()))

        when:
        def details = provider.getDetails(token)

        then:
        !details.present
    }
}
