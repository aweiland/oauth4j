package io.github.aweiland.oauth4j.provider;


import io.github.aweiland.oauth4j.SocialProvider;
import io.github.aweiland.oauth4j.support.AppDataHolder;
import io.github.aweiland.oauth4j.support.OAuth1Info;
import io.github.aweiland.oauth4j.support.ProviderDetails;

import java.util.Optional;

public abstract class OAuth1Provider extends SocialProvider {


    public OAuth1Provider(String name, String displayName, String appId, String appSecret, String authUri, String accessTokenUri, String apiUri) {
        super(name, displayName, appId, appSecret, authUri, accessTokenUri, apiUri);
    }


}
