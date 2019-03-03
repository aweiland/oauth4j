package com.github.aweiland.oauth4j.support;


public abstract class OAuthInfo implements TokenHolder {

    /**
     * SocialProvider of this auth info (eg facebook, twitter, etc)
     */
    protected String provider;


    public String getProvider() {
        return provider;
    }


}
