package io.github.aweiland.provider;


import io.github.aweiland.SocialProvider;
import io.github.aweiland.support.OAuth1Info;
import io.github.aweiland.support.ProviderDetails;

import java.util.Optional;

public abstract class OAuth1Provider extends SocialProvider<OAuth1Info> {


    public OAuth1Provider(String authUri, String atUri, String aUri) {
        super(authUri, atUri, aUri);
    }


    public abstract Optional<ProviderDetails> getProviderDetails(ProviderRequest req, String accessToken, String tokenSecret);
}
