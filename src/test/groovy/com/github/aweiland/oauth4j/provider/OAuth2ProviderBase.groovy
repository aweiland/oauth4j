package com.github.aweiland.oauth4j.provider

import com.github.aweiland.oauth4j.ProviderRegistry
import com.github.aweiland.oauth4j.provider.flow.AuthVerify
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.aweiland.oauth4j.support.OAuth2Info
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
        def req = new AuthVerify.Builder().appId("asf").appSecret("asf").returnUri("sfadf").code("sfasf").build()

        and: "Mocked endpoints will fail"
        wireMock.register(post(urlEqualTo("/token"))
                .willReturn(badRequest()))

        when:
        def token = provider.verify(req)

        then:
        !token.present
    }

    def "Test failed getting provider details"() {
        given:
        def provider = createProvider()

        and: "Valid token"
        and: "A valid token"
        def token = new OAuth2Info.Builder()
                .provider(provider.getName())
                .tokenType("Bearer")
                .accessToken("asfasdfsadfa")
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
