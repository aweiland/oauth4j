package io.github.aweiland.oauth4j.provider.flow;

import io.github.aweiland.oauth4j.support.*;

public class AuthVerify implements CodeHolder, RequestTokenHolder, AppDataHolder, ReturnUriHolder {

    private final String code;
    private final String requestToken;
    private final String appId;
    private final String appSecret;
    private final String returnUri;

    private AuthVerify(Builder builder) {
        appId = builder.appId;
        code = builder.code;
        requestToken = builder.requestToken;
        appSecret = builder.appSecret;
        returnUri = builder.returnUri;
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
    public String getCode() {
        return code;
    }

    @Override
    public String getRequestToken() {
        return requestToken;
    }

    @Override
    public String getReturnUri() {
        return returnUri;
    }


    public static final class Builder {
        private String appId;
        private String code;
        private String requestToken;
        private String appSecret;
        private String returnUri;

        public Builder() {
        }

        public Builder(AuthVerify copy) {
            this.appId = copy.appId;
            this.code = copy.code;
            this.requestToken = copy.requestToken;
            this.appSecret = copy.appSecret;
            this.returnUri = copy.returnUri;
        }

        public Builder appId(String val) {
            appId = val;
            return this;
        }

        public Builder code(String val) {
            code = val;
            return this;
        }

        public Builder requestToken(String val) {
            requestToken = val;
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

        public AuthVerify build() {
            return new AuthVerify(this);
        }
    }
}
