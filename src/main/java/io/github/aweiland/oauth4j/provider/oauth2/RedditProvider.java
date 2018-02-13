package io.github.aweiland.oauth4j.provider.oauth2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import io.github.aweiland.oauth4j.provider.flow.AuthStart;
import io.github.aweiland.oauth4j.provider.flow.AuthVerify;
import io.github.aweiland.oauth4j.provider.flow.StartRequest;
import io.github.aweiland.oauth4j.provider.OAuth2Provider;
import io.github.aweiland.oauth4j.support.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class RedditProvider extends OAuth2Provider {

    public static final String DEFAULT_AUTH_URI = "https://www.reddit.com/api/v1/authorize";
    private static final String DEFAULT_ACCESS_TOKEN_URI = "https://www.reddit.com/api/v1/access_token";
    private static final String DEFAULT_API_URI = "https://www.reddit.com/api/v1/me";
    private static final String DEFAULT_SCOPES = "identity";

    Logger LOGGER = LoggerFactory.getLogger(RedditProvider.class);


    public RedditProvider(String appId, String appSecret) {
        super("reddit", "Reddit", appId, appSecret, DEFAULT_AUTH_URI, DEFAULT_ACCESS_TOKEN_URI, DEFAULT_API_URI);
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
        // FB bday can be MM/DD/YYYY or MM/DD or YYYY
        try {
            final HttpResponse<RedditDetails> response = Unirest.get(this.getApiUri())
                .header("Authorization", "bearer " + accessToken.getToken())
                .asObject(RedditDetails.class);

            if (response.getStatus() == 200) {
                final RedditDetails details = response.getBody();

                return Optional.of(new ProviderDetails.Builder()
                        .provider(this.getName())
                        .displayName(details.data.name)
                        .providerId(details.data.id).build());
            } else {
                return Optional.empty();
            }

        } catch (Exception ex) {
            LOGGER.error("Failed getting Microsoft details", ex);
            return Optional.empty();
        }
    }


    private String getRedirectUri(StartRequest req) {
        return Unirest.get(this.getAuthUri()).queryString("client_id", this.getAppId())
                .queryString("redirect_uri", req.getReturnUri())
                .queryString("response_type", "code")
                .queryString("scope", DEFAULT_SCOPES)
                .getUrl();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RedditData {
        
        public String id;

        
        public String name;
    
    }

    // https://github.com/reddit-archive/reddit/wiki/JSON
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RedditDetails {

        public RedditData data;

    }
}
