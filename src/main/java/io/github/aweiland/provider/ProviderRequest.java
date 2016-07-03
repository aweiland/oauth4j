package io.github.aweiland.provider;

/**
 * Holds data on how to perform a request for a provider.  Oauth consumer keys, etc
 */
public class ProviderRequest {

    /**
     * SocialProvider key/consumer key
     */
    private String key;

    /**
     * SocialProvider key/consumer key
     */
    private String secret;

    /**
     * URI to redirect to at the provider to start the flow
     */
    private String redirectUri;

    /**
     * Ending URI.  The URI for when the oauth flow is complete.
     */
    private String finishUri;

    /**
     * Request token for oauth 1
     */
    private String requestToken;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getFinishUri() {
        return finishUri;
    }

    public void setFinishUri(String finishUri) {
        this.finishUri = finishUri;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }
}
