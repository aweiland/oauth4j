package io.github.aweiland.oauth4j.provider

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
        given:
        def provider = createProvider()
        def req = new ProviderRequest(finishUri: FINISH_URI)


        when:
        def red = provider.start(req)

        then:
        red.isPresent()
        println(red.get().redirectUri)
        red.get().redirectUri.equals("https://graph.facebook.com/v2.5/oauth/authorize?client_id={$CLIENT_ID}&redirect_uri=http%3A%2F%2Fsite.com%2Ffacebook%2Ffinish")
    }
}
