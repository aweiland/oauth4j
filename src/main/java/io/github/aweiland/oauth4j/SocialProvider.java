package io.github.aweiland.oauth4j;


import io.github.aweiland.oauth4j.provider.ProviderRequest;
import io.github.aweiland.oauth4j.support.AppDataHolder;
import io.github.aweiland.oauth4j.support.OAuthInfo;
import io.github.aweiland.oauth4j.support.ReturnUriHolder;
import io.github.aweiland.oauth4j.support.TokenHolder;

import java.util.Optional;

public abstract class SocialProvider<T extends OAuthInfo> {

    private String appId;
    private String appSecret;

    // todo make final and require in constructor
    private String name;


    public SocialProvider(String appId, String appSecret) {
        this.appId = appId;
        this.appSecret = appSecret;
    }


    /**
     * Start an OAuth authentication
     * @return
     */
    public abstract <T extends AppDataHolder & ReturnUriHolder> Optional<String> start(T req);

    /**
     * Verify, get access token, etc.  Return details
     * @return
     */
    public abstract Optional<T> verify(ProviderRequest req);


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

    protected void setName(String name) {
        this.name = name;
    }
}
