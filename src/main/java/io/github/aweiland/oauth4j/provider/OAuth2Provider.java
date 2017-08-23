package io.github.aweiland.oauth4j.provider;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.github.aweiland.oauth4j.SocialProvider;
import io.github.aweiland.oauth4j.support.AppDataHolder;
import io.github.aweiland.oauth4j.support.OAuth2Info;
import io.github.aweiland.oauth4j.support.ProviderDetails;
import io.github.aweiland.oauth4j.support.ReturnUriHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public abstract class OAuth2Provider extends SocialProvider<OAuth2Info> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2Provider.class);


    public OAuth2Provider(String name, String displayName, String appId, String appSecret) {
        super(name, displayName, appId, appSecret);
    }


    public abstract Optional<ProviderDetails> getProviderDetails(String accessToken);


    protected <T extends AppDataHolder & ReturnUriHolder> Optional<OAuth2Info> getAccessTokenAndDetails(String code, T req) {
        try {
            final HttpResponse<TokenResponse> json = Unirest.post(getAccessTokenUri())
//                    .header("Content-Type", "application/json")
//                    .header("Accept", "application/json")
                    .field("client_id", getAppId())
                    .field("client_secret", getAppSecret())
                    .field("redirect_uri", req.getReturnUri())
                    .field("code", code)
                    .field("grant_type", "authorization_code")
                    .asObject(TokenResponse.class);

            if (json.getStatus() != 200) {
                LOGGER.warn(json.getStatusText());
                return Optional.empty();
            }


            return Optional.ofNullable(getProviderDetails(json.getBody().access_token).map(details -> new OAuth2Info.Builder()
                    .provider(details.getProvider())
                    .identifier(details.getProviderId())
                    .accessToken(json.getBody().access_token)
                    .expiresIn(json.getBody().expires_in)
                    .tokenType(json.getBody().token_type)
                    .refreshToken(json.getBody().refresh_token)
                    .details(details)
                    .build()).orElse(null));


        } catch (UnirestException e) {
            return Optional.empty();
        }




//        Client client = ClientBuilder.newClient();
//        WebTarget target = client.target(getAccessTokenUri());

//        MultivaluedMap<String, String> body = new MultivaluedHashMap<>();
//        body.add("client_id", getAppId());
//        body.add("client_secret", getAppSecret());
//        body.add("redirect_uri", req.getReturnUri());
//        body.add("code", code);
//        body.add("grant_type", "authorization_code");

//        try {
//            final TokenResponse post = target.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.form(body), TokenResponse.class);
//
//            return Optional.ofNullable(getProviderDetails(post.access_token).map(details -> new OAuth2Info.Builder()
//                        .provider(details.getProvider())
//                        .identifier(details.getProviderId())
//                        .accessToken(post.access_token)
//                        .expiresIn(post.expires_in)
//                        .tokenType(post.token_type)
//                        .refreshToken(post.refresh_token)
//                        .details(details)
//                        .build()).orElse(null));
//        } catch (Exception ex) {
//            LOGGER.error("Failed to exchange code for token", ex);
//            return Optional.empty();
//        }
    }

    /**
     * OAuth 2 code -> Token response holder
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class TokenResponse {
        public String access_token;
        public String token_type;
        public String refresh_token;
        public Integer expires_in;
    }


}
