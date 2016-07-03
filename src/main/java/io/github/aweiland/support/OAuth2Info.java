package io.github.aweiland.support;

/**
 * Created by aweiland on 1/29/16.
 */
public class OAuth2Info extends OAuthInfo {

    private final String accessToken;
    private final String tokenType;
    private final Integer expiresIn;
    private final String refreshToken;

    private OAuth2Info(Builder builder) {
        provider = builder.provider;
        identifier = builder.identifier;
        details = builder.details;
        accessToken = builder.accessToken;
        tokenType = builder.tokenType;
        expiresIn = builder.expiresIn;
        refreshToken = builder.refreshToken;
    }


    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }


    public static final class Builder {
        private String provider;
        private String identifier;
        private ProviderDetails details;
        private String accessToken;
        private String tokenType;
        private Integer expiresIn;
        private String refreshToken;

        public Builder() {
        }

        public Builder(OAuth2Info copy) {
            this.provider = copy.provider;
            this.identifier = copy.identifier;
            this.details = copy.details;
            this.accessToken = copy.accessToken;
            this.tokenType = copy.tokenType;
            this.expiresIn = copy.expiresIn;
            this.refreshToken = copy.refreshToken;
        }

        public Builder provider(String val) {
            provider = val;
            return this;
        }

        public Builder identifier(String val) {
            identifier = val;
            return this;
        }

        public Builder details(ProviderDetails val) {
            details = val;
            return this;
        }

        public Builder accessToken(String val) {
            accessToken = val;
            return this;
        }

        public Builder tokenType(String val) {
            tokenType = val;
            return this;
        }

        public Builder expiresIn(Integer val) {
            expiresIn = val;
            return this;
        }

        public Builder refreshToken(String val) {
            refreshToken = val;
            return this;
        }

        public OAuth2Info build() {
            return new OAuth2Info(this);
        }
    }
}
