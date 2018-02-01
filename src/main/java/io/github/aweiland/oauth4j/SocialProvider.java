package io.github.aweiland.oauth4j;


import io.github.aweiland.oauth4j.provider.flow.AuthStart;
import io.github.aweiland.oauth4j.provider.flow.AuthVerify;
import io.github.aweiland.oauth4j.provider.flow.StartRequest;
import io.github.aweiland.oauth4j.support.OAuthInfo;

import java.util.Optional;

public abstract class SocialProvider<T extends OAuthInfo, U extends TokenHolder> {

    private String appId;
    private String appSecret;

    private String authUri;
    private String accessTokenUri;
    private String apiUri;

    
    private final String name;
    private final String displayName;


    public SocialProvider(String name, String displayName, String appId, String appSecret, String authUri, String accessTokenUri, String apiUri) {
        this.name = name;
        this.displayName = displayName;
        this.appId = appId;
        this.appSecret = appSecret;
        this.authUri = authUri;
        this.accessTokenUri = accessTokenUri;
        this.apiUri = apiUri;
    }


    /**
     * Start an OAuth authentication
     * @return
     */
    public abstract Optional<AuthStart> start(StartRequest req);

    /**
     * Verify, get access token, etc.
     * @return
     */
    public abstract Optional<U> verify(AuthVerify req);
    
    
    /**
     * Use the access token to get user details
     */
    public abstract Optional<ProviderDetails> getDetails(U accessToken);


    protected String getAuthUri() {
        return this.authUri;
    }
    
    protected void setAuthUri(String uri) {
        this.authUri = uri;
    }
    
    protected String getAccessTokenUri() {
        return this.accessTokenUri;
    }
    
    protected void setAccessTokenUri(String uri) {
        this.accessTokenUri = uri;
    }
    
    protected String getApiUri() {
        return this.apiUri;
    }
    
    protected void setApiUri(String uri) {
        this.apiUri = uri;
    }


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }


    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }
}
