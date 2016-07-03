package io.github.aweiland.provider.oauth2;

import com.icyjupiter.api.auth.social.provider.OAuth2Provider;
import com.icyjupiter.api.auth.social.provider.ProviderRequest;
import com.icyjupiter.api.auth.social.support.OAuth2Info;
import com.icyjupiter.api.auth.social.support.ProviderDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.util.Optional;

public class DropboxProvider extends OAuth2Provider {
    Logger LOGGER = LoggerFactory.getLogger(DropboxProvider.class);


    public DropboxProvider(String authUri, String atUri, String aUri) {
        super(authUri, atUri, aUri);
        setName("dropbox");
    }

    @Override
    public Optional<ProviderRequest> start(ProviderRequest req, HttpServletRequest request) {
        req.setRedirectUri(getRedirectUri(req));
        return Optional.of(req);
    }

    @Override
    public Optional<OAuth2Info> verify(ProviderRequest req, HttpServletRequest request) {
        Optional<String> code = getCode(request);
        return code.map(s -> getAccessTokenAndDetails(s, req)).orElse(Optional.empty());
    }

    @Override
    public Optional<ProviderDetails> getProviderDetails(ProviderRequest req, String accessToken) {
        Client client = ClientBuilder.newClient();
        final WebTarget target = client.target(getApiUri())
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

    String getRedirectUri(ProviderRequest req) {
        return UriBuilder.fromUri(getAuthorizationUri())
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
