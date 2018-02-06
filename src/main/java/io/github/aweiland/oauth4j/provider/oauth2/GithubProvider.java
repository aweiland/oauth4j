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

public class GithubProvider extends OAuth2Provider {

    public static final String DEFAULT_AUTH_URI = "https://github.com/login/oauth/authorize";
    private static final String DEFAULT_ACCESS_TOKEN_URI = "https://github.com/login/oauth/access_token";
    private static final String DEFAULT_API_URI = "https://api.github.com/user";
    private static final String DEFAULT_SCOPES = "read:user";

    Logger LOGGER = LoggerFactory.getLogger(GithubProvider.class);
    
    private String scopes = DEFAULT_SCOPES;
    public String getScopes() { return scopes; }
    public void setScopes(String scopes) { this.scopes = scopes; }


    public GithubProvider(String appId, String appSecret) {
        super("github", "GitHub", appId, appSecret, DEFAULT_AUTH_URI, DEFAULT_ACCESS_TOKEN_URI, DEFAULT_API_URI);
    }

    @Override
    public Optional<AuthStart> start(StartRequest req) {
        AuthStart start = new AuthStart.Builder().redirectUri(getRedirectUri(req)).build();
        return Optional.of(start);
    }


    @Override
    public Optional<OAuth2Info> verify(AuthVerify req) {
        Optional<String> code = Optional.ofNullable(req.getCode());
        return code.map(s -> performCodeExchange(s, req)).orElse(Optional.empty());
    }

    @Override
    public Optional<ProviderDetails> getDetails(OAuth2Info accessToken) {
        // FB bday can be MM/DD/YYYY or MM/DD or YYYY
        try {
            final HttpResponse<GithubDetails> response = Unirest.get(this.getApiUri()).queryString("access_token", accessToken.getToken())
                    .asObject(GithubDetails.class);

            if (response.getStatus() == 200) {
                final GithubDetails details = response.getBody();

                return Optional.of(new ProviderDetails.Builder()
                        .provider("github")
                        .displayName(details.name)
                        .firstName(details.login)
                        .providerId(details.id).build());
            } else {
                return Optional.empty();
            }

        } catch (Exception ex) {
            LOGGER.error("Failed getting GitHub details", ex);
            return Optional.empty();
        }
    }


    private String getRedirectUri(StartRequest req) {
        return Unirest.get(this.getAuthUri()).queryString("client_id", this.getAppId())
                .queryString("redirect_uri", req.getReturnUri())
                .queryString("scope", this.getScopes())
                .getUrl();
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GithubDetails {

        public String id;

        public String name;
        
        public String login;


    }
}
