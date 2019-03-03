package com.github.aweiland.oauth4j.provider.oauth2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.github.aweiland.oauth4j.provider.flow.AuthStart;
import com.github.aweiland.oauth4j.provider.flow.AuthVerify;
import com.github.aweiland.oauth4j.provider.flow.StartRequest;
import com.github.aweiland.oauth4j.provider.OAuth2Provider;
import com.github.aweiland.oauth4j.support.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class YahooProvider extends OAuth2Provider {

    public static final String DEFAULT_AUTH_URI = "https://api.login.yahoo.com/oauth2/request_auth";
    private static final String DEFAULT_ACCESS_TOKEN_URI = "https://api.login.yahoo.com/oauth2/get_token";
    private static final String DEFAULT_API_URI = "https://social.yahooapis.com/v1/user/me/profile/tinyusercard";
    private static final String DEFAULT_SCOPES = "identity";
    
    // TODO someday add refresh token refresh flow to providers

    Logger LOGGER = LoggerFactory.getLogger(YahooProvider.class);


    public YahooProvider(String appId, String appSecret) {
        super("yahoo", "Yahoo", appId, appSecret, DEFAULT_AUTH_URI, DEFAULT_ACCESS_TOKEN_URI, DEFAULT_API_URI);
    }

    @Override
    public Optional<AuthStart> start(StartRequest req) {
        AuthStart start = new AuthStart.Builder().redirectUri(getRedirectUri(req)).build();
        return Optional.of(start);
    }


    @Override
    public Optional<OAuthInfo> verify(AuthVerify req) {
        Optional<String> code = Optional.ofNullable(req.getCode());
        return code.map(s -> performCodeExchange(s, req)).orElse(Optional.empty());
    }

    @Override
    public Optional<ProviderDetails> getDetails(OAuthInfo accessToken) {
        try {
            final HttpResponse<YahooDetails> response = Unirest.get(this.getApiUri())
                .header("Authorization", "bearer " + accessToken.getToken())
                .asObject(YahooDetails.class);

            if (response.getStatus() == 200) {
                final YahooDetails details = response.getBody();

                return Optional.of(new ProviderDetails.Builder()
                        .provider(this.getName())
                        .displayName(details.profile.nickname)
                        .providerId(details.profile.guid).build());
            } else {
                return Optional.empty();
            }

        } catch (Exception ex) {
            LOGGER.error("Failed getting Yahoo details", ex);
            return Optional.empty();
        }
    }


    private String getRedirectUri(StartRequest req) {
        return Unirest.get(this.getAuthUri()).queryString("client_id", this.getAppId())
                .queryString("redirect_uri", req.getReturnUri())
                .queryString("response_type", "code")
                .getUrl();
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class YahooProfile {
        public String guid;
        
        public String nickname;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class YahooDetails {
        public YahooProfile profile;

    }
}
