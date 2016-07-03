package io.github.aweiland;


import com.icyjupiter.api.auth.social.provider.ProviderRequest;
import com.icyjupiter.api.auth.social.support.OAuthInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public abstract class SocialProvider<T extends OAuthInfo> {

    private String authorizationUri;
    private String accessTokenUri;
    private String apiUri;

    // todo make final and require in constructor
    private String name;

    public SocialProvider(String authUri, String atUri, String aUri) {
        authorizationUri = authUri;
        accessTokenUri = atUri;
        apiUri = aUri;
    }


    /**
     * Start an OAuth authentication
     * @return
     */
    public abstract Optional<ProviderRequest> start(ProviderRequest req, HttpServletRequest request);

    /**
     * Verify, get access token, etc.  Return details
     * @return
     */
    public abstract Optional<T> verify(ProviderRequest req, HttpServletRequest request);


    public String getAuthorizationUri() {
        return authorizationUri;
    }

    public void setAuthorizationUri(String authorizationUri) {
        this.authorizationUri = authorizationUri;
    }

    public String getAccessTokenUri() {
        return accessTokenUri;
    }

    public void setAccessTokenUri(String accessTokenUri) {
        this.accessTokenUri = accessTokenUri;
    }

    public String getApiUri() {
        return apiUri;
    }

    public void setApiUri(String apiUri) {
        this.apiUri = apiUri;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }
}
