package com.github.aweiland.oauth4j.provider;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.github.aweiland.oauth4j.SocialProvider;
import com.github.aweiland.oauth4j.support.AppDataHolder;
import com.github.aweiland.oauth4j.support.OAuth2Info;
import com.github.aweiland.oauth4j.support.ReturnUriHolder;
import com.github.aweiland.oauth4j.support.OAuthInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public abstract class OAuth2Provider extends SocialProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2Provider.class);


    public OAuth2Provider(String name, String displayName, String appId, String appSecret, String authUri, String accessTokenUri, String apiUri) {
        super(name, displayName, appId, appSecret, authUri, accessTokenUri, apiUri);
    }


    protected <T extends AppDataHolder & ReturnUriHolder> Optional<OAuthInfo> performCodeExchange(String code, T req) {
        try {
            final HttpResponse<TokenResponse> json = Unirest.post(getAccessTokenUri())
                    .header("Accept", "application/json")
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


            return Optional.of(new OAuth2Info.Builder()
                    .provider(this.getName())
                    .accessToken(json.getBody().access_token)
                    .expiresIn(json.getBody().expires_in)
                    .tokenType(json.getBody().token_type)
                    .refreshToken(json.getBody().refresh_token)
                    .build());


        } catch (UnirestException e) {
            LOGGER.error("Failed to verify code", e);
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
