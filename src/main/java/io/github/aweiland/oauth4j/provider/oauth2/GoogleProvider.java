package io.github.aweiland.oauth4j.provider.oauth2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import io.github.aweiland.oauth4j.provider.OAuth2Provider;
import io.github.aweiland.oauth4j.provider.flow.AuthStart;
import io.github.aweiland.oauth4j.provider.flow.AuthVerify;
import io.github.aweiland.oauth4j.provider.flow.StartRequest;
import io.github.aweiland.oauth4j.support.OAuth2Info;
import io.github.aweiland.oauth4j.support.OAuthInfo;
import io.github.aweiland.oauth4j.support.ProviderDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class GoogleProvider extends OAuth2Provider {

    Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final String DEFAULT_AUTH_URI = "https://accounts.google.com/o/oauth2/auth";
    private static final String DEFAULT_ACCESS_TOKEN_URI = "https://www.googleapis.com/oauth2/v4/token";
    private static final String DEFAULT_API_URI = "https://www.googleapis.com/plus/v1/people/me";


    public GoogleProvider(String appId, String appSecret) {
        super("google", "Google", appId, appSecret, DEFAULT_AUTH_URI, DEFAULT_ACCESS_TOKEN_URI, DEFAULT_API_URI);
    }

    @Override
    public Optional<AuthStart> start(StartRequest req) {
        return Optional.of(new AuthStart.Builder().redirectUri(getRedirectUri(req)).build());
    }

    @Override
    public Optional<OAuthInfo> verify(AuthVerify req) {
        Optional<String> code = Optional.ofNullable(req.getCode());
        return code.map(s -> performCodeExchange(s, req)).orElse(Optional.empty());
    }

    @Override
    public Optional<ProviderDetails> getDetails(OAuthInfo accessToken) {
        try {
            final HttpResponse<GoogleDetails> response = Unirest.get(getApiUri())
                    .queryString("access_token", accessToken)
                    .asObject(GoogleDetails.class);

            if (response.getStatus() == 200) {
                final GoogleDetails details = response.getBody();
                return Optional.of(new ProviderDetails.Builder()
                        .provider("google")
                        .providerId(details.id)
                        .displayName(details.displayName)
                        .build());
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            return Optional.empty();
        }

    }

   
    private String getRedirectUri(StartRequest req) {
        return Unirest.get(getAuthUri())
                .queryString("client_id", this.getAppId())
                .queryString("redirect_uri", req.getReturnUri())
                .queryString("response_type", "code")
                .queryString("scope", req.getScopes().orElseThrow(() -> { return new IllegalArgumentException("Scopes are required"); }))
                .getUrl();

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GoogleDetails {
        public String id;

        public String displayName;
    }
}
