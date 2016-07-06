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

public class DropboxProvider extends OAuth2Provider {
    Logger LOGGER = LoggerFactory.getLogger(DropboxProvider.class);

    private static final String AUTH_URI = "https://www.dropbox.com/1/oauth2/authorize";
    private static final String ACCESS_TOKEN_URI = "https://api.dropbox.com/1/oauth2/token";
    private static final String API_URI = "http://api.dropboxapi.com/1/account/info";



    public DropboxProvider(String appId, String appSecret) {
        super(appId, appSecret);
        setName("dropbox");
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
            final DropboxDetails details = target.request(MediaType.APPLICATION_JSON_TYPE).get(DropboxDetails.class);
            return Optional.of(new ProviderDetails.Builder()
                    .provider("dropbox")
                    .providerId(details.uid)
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

    String getRedirectUri(ProviderRequest req) {
        return UriBuilder.fromUri(getAppId())
                .queryParam("client_id", req.getKey())
                .queryParam("redirect_uri", req.getFinishUri())
                .queryParam("response_type", "code")
                .build().toString();
    }


    public static class DropboxDetails {

        String uid;

        String displayName;

    }

}
