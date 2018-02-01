package io.github.aweiland.oauth4j.support;

/**
 * Created by aweiland on 1/29/16.
 */
public class OAuth1Info extends OAuthInfo {

    private final String token;
    private final String secret;

    private OAuth1Info(Builder builder) {
        provider = builder.provider;
        identifier = builder.identifier;
        token = builder.token;
        secret = builder.secret;
    }


    public String getToken() {
        return token;
    }

    public String getSecret() {
        return secret;
    }


    public static final class Builder {
        private String provider;
        private String identifier;
        private String token;
        private String secret;

        public Builder() {
        }

        public Builder(OAuth1Info copy) {
            this.provider = copy.provider;
            this.identifier = copy.identifier;
            this.token = copy.token;
            this.secret = copy.secret;
        }

        public Builder provider(String val) {
            provider = val;
            return this;
        }

        public Builder identifier(String val) {
            identifier = val;
            return this;
        }


        public Builder token(String val) {
            token = val;
            return this;
        }

        public Builder secret(String val) {
            secret = val;
            return this;
        }

        public OAuth1Info build() {
            return new OAuth1Info(this);
        }
    }
}

