package io.github.aweiland.oauth4j;


import io.github.aweiland.oauth4j.provider.flow.AuthStart;
import io.github.aweiland.oauth4j.provider.flow.AuthVerify;
import io.github.aweiland.oauth4j.provider.flow.StartRequest;
import io.github.aweiland.oauth4j.support.OAuthInfo;

import java.util.Optional;

public abstract class SocialProvider<T extends OAuthInfo> {

    private String appId;
    private String appSecret;

    // todo make final and require in constructor
    private final String name;
    private final String displayName;


    public SocialProvider(String name, String displayName, String appId, String appSecret) {
        this.name = name;
        this.displayName = displayName;
        this.appId = appId;
        this.appSecret = appSecret;
    }


    /**
     * Start an OAuth authentication
     * @return
     */
    public abstract Optional<AuthStart> start(StartRequest req);

    /**
     * Verify, get access token, etc.  Return details
     * @return
     */
    public abstract Optional<T> verify(AuthVerify req);


    protected abstract String getAuthUri();
    protected abstract String getAccessTokenUri();


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

}
