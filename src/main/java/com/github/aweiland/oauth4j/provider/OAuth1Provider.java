package com.github.aweiland.oauth4j.provider;


import com.github.aweiland.oauth4j.SocialProvider;

public abstract class OAuth1Provider extends SocialProvider {


    public OAuth1Provider(String name, String displayName, String appId, String appSecret, String authUri, String accessTokenUri, String apiUri) {
        super(name, displayName, appId, appSecret, authUri, accessTokenUri, apiUri);
    }


}
