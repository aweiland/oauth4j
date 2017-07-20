package io.github.aweiland.oauth4j.provider.oauth2;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import io.github.aweiland.oauth4j.provider.OAuth2Provider;
import io.github.aweiland.oauth4j.provider.flow.AuthStart;
import io.github.aweiland.oauth4j.provider.flow.AuthVerify;
import io.github.aweiland.oauth4j.provider.flow.StartRequest;
import io.github.aweiland.oauth4j.support.OAuth2Info;
import io.github.aweiland.oauth4j.support.ProviderDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.WebTarget;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.UriBuilder;
import java.util.Optional;

public class GoogleProvider extends OAuth2Provider {

    Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final String AUTH_URI = "https://accounts.google.com/o/oauth2/auth";
    private static final String ACCESS_TOKEN_URI = "https://www.googleapis.com/oauth2/v3/token";
    private static final String API_URI = "https://www.googleapis.com/plus/v1/people/me";


    public GoogleProvider(String appId, String appSecret) {
        super("google", "Google", appId, appSecret);
    }

    @Override
    public Optional<AuthStart> start(StartRequest req) {
        return Optional.of(new AuthStart.Builder().redirectUri(getRedirectUri(req)).build());
    }

    @Override
    public Optional<OAuth2Info> verify(AuthVerify req) {
        Optional<String> code = Optional.ofNullable(req.getCode());
        return code.map(s -> getAccessTokenAndDetails(s, req)).orElse(Optional.empty());
    }

    @Override
    public Optional<ProviderDetails> getProviderDetails(String accessToken) {
//        Client client = ClientBuilder.newClient();
//        final WebTarget target = client.target(API_URI)
//                .queryParam("access_token", accessToken);

        try {
            final HttpResponse<GoogleDetails> response = Unirest.get(API_URI)
                    .queryString("access_token", accessToken)
                    .header("Accept", "application/json")
                    .asObject(GoogleDetails.class);

//            final GoogleDetails details = target.request(MediaType.APPLICATION_JSON_TYPE).get(GoogleDetails.class);
            final GoogleDetails details = response.getBody();
            return Optional.of(new ProviderDetails.Builder()
                    .provider("google")
                    .providerId(details.id)
                    .displayName(details.displayName)
                    .build());
        } catch (Exception e) {
            return Optional.empty();
        }

    }

    @Override
    protected String getAuthUri() {
        return AUTH_URI;
    }

    @Override
    protected String getAccessTokenUri() {
        return ACCESS_TOKEN_URI;
    }

    private String getRedirectUri(StartRequest req) {
        return Unirest.get(getAuthUri())
                .queryString("client_id", this.getAppId())
                .queryString("redirect_uri", req.getReturnUri())
                .queryString("response_type", "code")
                .queryString("scope", req.getScopes().orElseThrow(() -> { return new IllegalArgumentException("Scopes are required"); }))
                .getUrl();

//        return UriBuilder.fromUri(getAuthUri())
//                .queryParam("client_id", getAppId())
//                .queryParam("redirect_uri", req.getReturnUri())
//                .queryParam("response_type", "code")
//                .queryParam("scope", req.getScopes().orElseThrow(() -> { return new IllegalArgumentException("Scopes are required"); }))
//                .build().toString();
    }

    public static class GoogleDetails {
        String id;

        String displayName;
    }
}
