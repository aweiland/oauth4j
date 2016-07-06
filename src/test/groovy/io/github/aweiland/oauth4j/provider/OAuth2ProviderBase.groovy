package io.github.aweiland.oauth4j.provider

import spock.lang.Specification


abstract class OAuth2ProviderBase<P extends OAuth2Provider> extends Specification {

    abstract P createProvider()

    def "test provider is configured"() {
        setup:
        def provider = createProvider()

        expect:
        provider?.appSecret?.trim()
        provider?.appId?.trim()
    }



//    def "Test got access token"() {
//        setup:
//        def provider = createProvider()
//        def http = Mock(HTTPBuilder)
//        provider.createHttpClient(_) >> http
//
//        when:
//        def token = provider.getAccessTokenAndDetails("code")
//
//        then:
//        token.present
//        1 * http.post(_ as Map, _ as Closure) >> new OAuth2Info()
//    }
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
