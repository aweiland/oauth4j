package io.github.aweiland.oauth4j.support;


public abstract class OAuthInfo {

    /**
     * SocialProvider of this auth info (eg facebook, twitter, etc)
     */
    protected String provider;

    /**
     * Identifier at the provider (facebook graphid, etc)
     */
    protected String identifier;



    public String getProvider() {
        return provider;
    }

    public String getIdentifier() {
        return identifier;
    }

}
