package io.github.aweiland.oauth4j.provider;


import io.github.aweiland.oauth4j.SocialProvider;
import io.github.aweiland.oauth4j.support.OAuth1Info;
import io.github.aweiland.oauth4j.support.ProviderDetails;

import java.util.Optional;

public abstract class OAuth1Provider extends SocialProvider<OAuth1Info> {


    public OAuth1Provider(String appId, String appSecret) {
        super(appId, appSecret);
    }


    public abstract Optional<ProviderDetails> getProviderDetails(ProviderRequest req, String accessToken, String tokenSecret);
}
