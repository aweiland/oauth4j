package io.github.aweiland.provider;


import com.icyjupiter.api.auth.social.SocialProvider;
import com.icyjupiter.api.auth.social.support.OAuth1Info;
import com.icyjupiter.api.auth.social.support.ProviderDetails;

import java.util.Optional;

public abstract class OAuth1Provider extends SocialProvider<OAuth1Info> {


    public OAuth1Provider(String authUri, String atUri, String aUri) {
        super(authUri, atUri, aUri);
    }


    public abstract Optional<ProviderDetails> getProviderDetails(ProviderRequest req, String accessToken, String tokenSecret);
}
