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

    public static final String AUTH_URI = "https://graph.facebook.com/v2.5/oauth/authorize";
    private static final String ACCESS_TOKEN_URI = "https://graph.facebook.com/v2.5/oauth/access_token";
    private static final String API_URI = "https://graph.facebook.com/v2.5/me";

    Logger LOGGER = LoggerFactory.getLogger(FacebookProvider.class);


    public FacebookProvider(String appId, String appSecret) {
        super("facebook", "Facebook", appId, appSecret);
    }

    @Override
    public Optional<AuthStart> start(StartRequest req) {
        AuthStart start = new AuthStart.Builder().redirectUri(getRedirectUri(req)).build();
        return Optional.of(start);
    }


    @Override
    public Optional<OAuth2Info> verify(AuthVerify req) {
        Optional<String> code = Optional.ofNullable(req.getCode());
        return code.map(s -> getAccessTokenAndDetails(s, req)).orElse(Optional.empty());
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
        return Unirest.get(getAuthUri()).queryString("client_id", this.getAppId())
                .queryString("redirect_uri", req.getReturnUri())
                .getUrl();
//        return UriBuilder.fromUri(getAuthUri())
//                .queryParam("client_id", this.getAppId())
//                .queryParam("redirect_uri", req.getReturnUri())
//                .build().toString();
    }

    @Override
    public Optional<ProviderDetails> getProviderDetails(String accessToken) {
//        Client client = ClientBuilder.newClient();
//
//        final WebTarget target = client.target(API_URI)
//                .queryParam("access_token", accessToken)
//                .queryParam("fields", "name,first_name,last_name,picture");


        // FB bday can be MM/DD/YYYY or MM/DD or YYYY
        try {
//            final FacebookDetails details = target.request(MediaType.APPLICATION_JSON_TYPE).get(FacebookDetails.class);
            final HttpResponse<FacebookDetails> response = Unirest.get(API_URI).queryString("access_token", accessToken)
                    .queryString("fields", "name,first_name,last_name,picture").asObject(FacebookDetails.class);

            final FacebookDetails details = response.getBody();

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
