package io.github.aweiland.oauth4j.provider

import io.github.aweiland.oauth4j.provider.flow.StartRequest
import io.github.aweiland.oauth4j.provider.oauth2.FacebookProvider
import org.mockserver.client.server.MockServerClient


class FacebookProviderSpec extends OAuth2ProviderBase<FacebookProvider> {

    final CLIENT_ID = "dsafwgqsdfasfasf"
    final CLIENT_SECRET = "985835t3-sdfasfads"
    final AUTH_URI = "https://graph.facebook.com/v2.3/oauth/authorize"
    final TOKEN_URI = "https://graph.facebook.com/v2.3/oauth/access_token"
    final API_URI = "https://graph.facebook.com/v2.3"

    final FINISH_URI = "http://site.com/facebook/finish"

    @Override
    FacebookProvider createProvider() {
        def provider = new FacebookProvider(CLIENT_ID, CLIENT_SECRET)
        return provider
    }

    @Override
    MockServerClient createMockServerForToken() {
        return null
    }

    def "Test get Redirect URI from start"() {
        given:
        def provider = createProvider()
        def req = new StartRequest.Builder().appId(CLIENT_ID).appSecret(CLIENT_SECRET).returnUri(FINISH_URI).build()
        def encodedFinish = URLEncoder.encode(FINISH_URI, "UTF-8")
        def authUri = FacebookProvider.AUTH_URI

        when:
        def red = provider.start(req)

        then:
        red.present
        def authStart = red.get()

        and:
        authStart.redirectUri != null
        authStart.redirectUri == "${authUri}?client_id=${CLIENT_ID}&redirect_uri=${encodedFinish}"
    }
}
