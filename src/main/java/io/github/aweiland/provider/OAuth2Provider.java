package io.github.aweiland.provider;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.deploy.config.ClientConfig;
import io.github.aweiland.SocialProvider;
import io.github.aweiland.support.OAuth2Info;
import io.github.aweiland.support.ProviderDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Optional;

public abstract class OAuth2Provider extends SocialProvider<OAuth2Info> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2Provider.class);

    public OAuth2Provider(String authUri, String atUri, String aUri) {
        super(authUri, atUri, aUri);
    }


    public abstract Optional<ProviderDetails> getProviderDetails(ProviderRequest req, String accessToken);

    /**
     * See if there is a code parameter in the request
     * @param request
     * @return
     */
    protected Optional<String> getCode(HttpServletRequest request) {
        return Optional.ofNullable(request.getParameter("code"));
    }

    protected Optional<OAuth2Info> getAccessTokenAndDetails(String code, ProviderRequest req) {
        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(getAccessTokenUri());


        MultivaluedMap<String, String> body = new MultivaluedHashMap<>();
        body.add("client_id", req.getKey());
        body.add("client_secret", req.getSecret());
        body.add("redirect_uri", req.getFinishUri());
        body.add("code", code);
        body.add("grant_type", "authorization_code");

        try {
            final TokenResponse post = target.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.form(body), TokenResponse.class);

            return Optional.ofNullable(getProviderDetails(req, post.access_token).map(details -> new OAuth2Info.Builder()
                        .provider(details.getProvider())
                        .identifier(details.getProviderId())
                        .accessToken(post.access_token)
                        .expiresIn(post.expires_in)
                        .tokenType(post.token_type)
                        .refreshToken(post.refresh_token)
                        .details(details)
                        .build()).orElse(null));
        } catch (Exception ex) {
            LOGGER.error("Failed to exchange code for token", ex);
            return Optional.empty();
        }
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
