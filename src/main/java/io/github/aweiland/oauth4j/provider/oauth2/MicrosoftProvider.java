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

public class MicrosoftProvider extends OAuth2Provider {

    public static final String DEFAULT_AUTH_URI = "https://login.live.com/oauth20_authorize.srf";
    private static final String DEFAULT_ACCESS_TOKEN_URI = "https://login.live.com/oauth20_token.srf";
    private static final String DEFAULT_API_URI = "https://apis.live.net/v5.0/me";
    private static final String DEFAULT_SCOPES = "wl.basic";

    Logger LOGGER = LoggerFactory.getLogger(MicrosoftProvider.class);


    public MicrosoftProvider(String appId, String appSecret) {
        super("microsoft", "Microsoft", appId, appSecret, DEFAULT_AUTH_URI, DEFAULT_ACCESS_TOKEN_URI, DEFAULT_API_URI);
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
            final HttpResponse<MicrosoftDetails> response = Unirest.get(this.getApiUri()).queryString("access_token", accessToken.getToken())
                    .asObject(MicrosoftDetails.class);

            if (response.getStatus() == 200) {
                final MicrosoftDetails details = response.getBody();

                return Optional.of(new ProviderDetails.Builder()
                        .provider(this.getName())
                        .displayName(details.name)
                        .firstName(details.firstName)
                        .lastName(details.lastName)
                        .providerId(details.id).build());
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
    public static class MicrosoftDetails {

        public String id;

        public String name;

        @JsonProperty("first_name")
        public String firstName;

        @JsonProperty("last_name")
        String lastName;


    }
}
