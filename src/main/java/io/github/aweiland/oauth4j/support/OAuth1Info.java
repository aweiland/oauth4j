package io.github.aweiland.oauth4j.support;

/**
 *
 */
public class OAuth1Info extends OAuthInfo {

    private final String token;
    private final String secret;

    private OAuth1Info(Builder builder) {
        provider = builder.provider;

        token = builder.token;
        secret = builder.secret;
    }


    public String getToken() {
        return token;
    }

    @Override
    public String getTokenType() {
        return null;
    }

    @Override
    public String getSecretToken() {
        return secret;
    }

    @Override
    public String getRefreshToken() {
        throw new UnsupportedOperationException("Secret not supported in OAuth 1.");
    }



    public static final class Builder {
        private String provider;
        private String token;
        private String secret;

        public Builder() {
        }

        public Builder(OAuth1Info copy) {
            this.provider = copy.provider;
            this.token = copy.token;
            this.secret = copy.secret;
        }

        public Builder provider(String val) {
            provider = val;
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

