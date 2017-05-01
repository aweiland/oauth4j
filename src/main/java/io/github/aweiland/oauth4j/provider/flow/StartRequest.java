package io.github.aweiland.oauth4j.provider.flow;

import io.github.aweiland.oauth4j.support.AppDataHolder;
import io.github.aweiland.oauth4j.support.ReturnUriHolder;
import io.github.aweiland.oauth4j.support.ScopesHolder;

import java.util.Optional;

/**
 * Holds the data to start an OAuth flow
 */
public final class StartRequest implements AppDataHolder, ReturnUriHolder {

    private final String appId;
    private final String appSecret;
    private final String returnUri;
    private final Optional<String> scopes;

    private StartRequest(Builder builder) {
        appId = builder.appId;
        appSecret = builder.appSecret;
        returnUri = builder.returnUri;
        scopes = builder.scopes;
    }


    @Override
    public String getAppId() {
        return appId;
    }

    @Override
    public String getAppSecret() {
        return appSecret;
    }

    @Override
    public String getReturnUri() {
        return returnUri;
    }

    public Optional<String> getScopes() {
        return scopes;
    }

    public static final class Builder {
        private String appId;
        private String appSecret;
        private String returnUri;
        private Optional<String> scopes = Optional.empty();

        public Builder() {
        }

        public Builder(StartRequest copy) {
            this.appId = copy.appId;
            this.appSecret = copy.appSecret;
            this.returnUri = copy.returnUri;
            this.scopes = copy.scopes;
        }

        public Builder appId(String val) {
            appId = val;
            return this;
        }

        public Builder appSecret(String val) {
            appSecret = val;
            return this;
        }

        public Builder returnUri(String val) {
            returnUri = val;
            return this;
        }

        public Builder scopes(Optional<String> val) {
            scopes = val;
            return this;
        }

        public StartRequest build() {
            return new StartRequest(this);
        }
    }
}
