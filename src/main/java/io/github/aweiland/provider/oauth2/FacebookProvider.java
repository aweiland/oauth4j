package io.github.aweiland.provider.oauth2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.aweiland.provider.OAuth2Provider;
import io.github.aweiland.provider.ProviderRequest;
import io.github.aweiland.support.OAuth2Info;
import io.github.aweiland.support.ProviderDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.util.Optional;

public class FacebookProvider extends OAuth2Provider {

    Logger LOGGER = LoggerFactory.getLogger(FacebookProvider.class);


    public FacebookProvider(String authUri, String atUri, String aUri) {
        super(authUri, atUri, aUri);
        setName("facebook");
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


    private String getRedirectUri(ProviderRequest req) {
        return UriBuilder.fromUri(getAuthorizationUri())
                .queryParam("client_id", req.getKey())
                .queryParam("redirect_uri", req.getFinishUri())
                .build().toString();
    }

    @Override
    public Optional<ProviderDetails> getProviderDetails(ProviderRequest req, String accessToken) {
        Client client = ClientBuilder.newClient();
        final WebTarget target = client.target(getApiUri())
                .queryParam("access_token", accessToken)
                .queryParam("fields", "name,first_name,last_name,picture");


        // FB bday can be MM/DD/YYYY or MM/DD or YYYY
        try {
            final FacebookDetails details = target.request(MediaType.APPLICATION_JSON_TYPE).get(FacebookDetails.class);
            return Optional.of(new ProviderDetails.Builder()
                    .provider("facebook")
//                    .profileUri(resp.get("link").toString())
                    .displayName(details.name)
                    .firstName(details.firstName)
                    .lastName(details.lastName)
//                    .photoUri(NestedMapResolver.resolve(() -> {
//                        resp.get("picture").get("data") }))
                    .providerId(details.id).build());

        } catch (Exception ex) {
            LOGGER.error("Failed getting Facebook details", ex);
            return Optional.empty();
        }
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
