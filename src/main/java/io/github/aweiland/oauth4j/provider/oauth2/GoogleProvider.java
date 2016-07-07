package io.github.aweiland.oauth4j.provider.oauth2;

import io.github.aweiland.oauth4j.provider.OAuth2Provider;
import io.github.aweiland.oauth4j.provider.ProviderRequest;
import io.github.aweiland.oauth4j.support.OAuth2Info;
import io.github.aweiland.oauth4j.support.ProviderDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.util.Optional;

public class GoogleProvider extends OAuth2Provider {

    Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final String AUTH_URI = "https://accounts.google.com/o/oauth2/auth";
    private static final String ACCESS_TOKEN_URI = "https://www.googleapis.com/oauth2/v3/token";
    private static final String API_URI = "https://www.googleapis.com/plus/v1/people/me";


    public GoogleProvider(String appId, String appSecret) {
        super(appId, appSecret);
        setName("google");
    }

    @Override
    public Optional<ProviderRequest> start(ProviderRequest req) {
        req.setRedirectUri(getRedirectUri(req));
        return Optional.of(req);
    }

    @Override
    public Optional<OAuth2Info> verify(ProviderRequest req) {
        Optional<String> code = Optional.ofNullable(req.getCode());
        return code.map(s -> getAccessTokenAndDetails(s, req)).orElse(Optional.empty());
    }

    @Override
    public Optional<ProviderDetails> getProviderDetails(ProviderRequest req, String accessToken) {
        Client client = ClientBuilder.newClient();
        final WebTarget target = client.target(API_URI)
                .queryParam("access_token", accessToken);

        try {
            final GoogleDetails details = target.request(MediaType.APPLICATION_JSON_TYPE).get(GoogleDetails.class);
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

    private String getRedirectUri(ProviderRequest req) {
        return UriBuilder.fromUri(getAppId())
                .queryParam("client_id", getAppId())
                .queryParam("redirect_uri", req.getFinishUri())
                .queryParam("response_type", "code")
                .queryParam("scope", req.getScopes())
                .build().toString();
    }

    public static class GoogleDetails {
        String id;

        String displayName;
    }
}
