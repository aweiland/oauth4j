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

public class FacebookProvider extends OAuth2Provider {

    public static final String DEFAULT_AUTH_URI = "https://graph.facebook.com/v2.5/oauth/authorize";
    private static final String DEFAULT_ACCESS_TOKEN_URI = "https://graph.facebook.com/v2.5/oauth/access_token";
    private static final String DEFAULT_API_URI = "https://graph.facebook.com/v2.5/me";

    Logger LOGGER = LoggerFactory.getLogger(FacebookProvider.class);


    public FacebookProvider(String appId, String appSecret) {
        super("facebook", "Facebook", appId, appSecret, DEFAULT_AUTH_URI, DEFAULT_ACCESS_TOKEN_URI, DEFAULT_API_URI);
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
            final HttpResponse<FacebookDetails> response = Unirest.get(this.getApiUri()).queryString("access_token", accessToken.getToken())
                    .queryString("fields", "name,first_name,last_name,picture").asObject(FacebookDetails.class);

            final FacebookDetails details = response.getBody();

            return Optional.of(new ProviderDetails.Builder()
                    .provider("facebook")
                    .displayName(details.name)
                    .firstName(details.firstName)
                    .lastName(details.lastName)
                    .providerId(details.id).build());

        } catch (Exception ex) {
            LOGGER.error("Failed getting Facebook details", ex);
            return Optional.empty();
        }
    }


    private String getRedirectUri(StartRequest req) {
        return Unirest.get(this.getAuthUri()).queryString("client_id", this.getAppId())
                .queryString("redirect_uri", req.getReturnUri())
                .getUrl();
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FacebookDetails {

        public String id;

        public String name;

        @JsonProperty("first_name")
        public String firstName;

        @JsonProperty("last_name")
        String lastName;


    }
}
