package io.github.aweiland.oauth4j.provider;

/**
 * Holds data on how to perform a request for a provider.  Oauth consumer keys, etc
 */
public class ProviderRequest {


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

    /**
     * OAuth 1 verifier
     */
    private String oauthVerifier;

    /**
     * OAuth 1 verifier token
     */
    private String oauthToken;

    /**
     * OAuth2 code for access token exchange
     */
    private String code;

    /**
     * OAuth Scopes
     */
    private String scopes;

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public String getOauthVerifier() {
        return oauthVerifier;
    }

    public void setOauthVerifier(String oauthVerifier) {
        this.oauthVerifier = oauthVerifier;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
