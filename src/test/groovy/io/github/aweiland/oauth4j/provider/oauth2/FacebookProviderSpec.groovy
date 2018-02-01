package io.github.aweiland.oauth4j.provider

import io.github.aweiland.oauth4j.provider.flow.StartRequest
import io.github.aweiland.oauth4j.provider.oauth2.FacebookProvider



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

    

    def "Test get Redirect URI from start"() {
        given: "A facebook provider"
        def provider = createProvider()
        def req = new StartRequest.Builder().appId(CLIENT_ID).appSecret(CLIENT_SECRET).returnUri(FINISH_URI).build()
        def encodedFinish = URLEncoder.encode(FINISH_URI, "UTF-8")
        def authUri = provider.authUri

        when: "A start request is created"
        def red = provider.start(req)

        then: "The request is generated"
        red.present
        def authStart = red.get()

        and: "The redirect uri is not null and is correct"
        authStart.redirectUri != null
        authStart.redirectUri == "${authUri}?client_id=${CLIENT_ID}&redirect_uri=${encodedFinish}"
    }
}
