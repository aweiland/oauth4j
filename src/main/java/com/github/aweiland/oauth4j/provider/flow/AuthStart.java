package com.github.aweiland.oauth4j.provider.flow;

import com.github.aweiland.oauth4j.support.RedirectUriHolder;

import java.util.Optional;

/**
 * Authorization start
 */
public class AuthStart implements RedirectUriHolder {

    private final String redirectUri;
    private final Optional<String> requestToken;

    private AuthStart(Builder builder) {
        redirectUri = builder.redirectUri;
        requestToken = builder.requestToken;
    }


    @Override
    public String getRedirectUri() {
        return redirectUri;
    }

    public Optional<String> getRequestToken() {
        return requestToken;
    }


    public static final class Builder {
        private String redirectUri;
        private Optional<String> requestToken = Optional.empty();

        public Builder() {
        }

        public Builder(AuthStart copy) {
            this.redirectUri = copy.redirectUri;
            this.requestToken = copy.requestToken;
        }

        public Builder redirectUri(String val) {
            redirectUri = val;
            return this;
        }

        public Builder requestToken(String val) {
            assert null != val : "Token cannot be null";

            requestToken = Optional.of(val);
            return this;
        }

        public AuthStart build() {
            return new AuthStart(this);
        }
    }
}
