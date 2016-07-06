package io.github.aweiland.oauth4j.provider

import io.github.aweiland.oauth4j.provider.oauth2.FacebookProvider


class FacebookProviderSpec extends OAuth2ProviderBase<FacebookProvider> {

    final CLIENT_ID = "dsafwgqsdfasfasf"
    final CLIENT_SECRET = "985835t3-sdfasfads"
    final AUTH_URI = "https://graph.facebook.com/v2.3/oauth/authorize"
    final TOKEN_URI = "https://graph.facebook.com/v2.3/oauth/access_token"
    final API_URI = "https://graph.facebook.com/v2.3"

    @Override
    FacebookProvider createProvider() {
        def provider = new FacebookProvider(CLIENT_ID, CLIENT_SECRET)
        return provider
    }
}
