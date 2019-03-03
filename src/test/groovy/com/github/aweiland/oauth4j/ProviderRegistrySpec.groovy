package com.github.aweiland.oauth4j

import com.github.aweiland.oauth4j.provider.oauth2.FacebookProvider
import com.github.aweiland.oauth4j.provider.oauth2.GoogleProvider
import spock.lang.Specification

class ProviderRegistrySpec extends Specification {
    def facebookProvider = new FacebookProvider("", "")
    def googleProvider = new GoogleProvider("", "")
    def providerRegistry = new ProviderRegistry([facebookProvider, googleProvider])

    def "test get not null"() {
        expect:
        providerRegistry.get("facebook").present
    }

    def "test equal"() {
        expect:
        providerRegistry.get("facebook").get() == facebookProvider
        providerRegistry.get("google").get() == googleProvider
    }

    def "test null string"() {
        expect:
        !providerRegistry.get(null).present
    }

}
