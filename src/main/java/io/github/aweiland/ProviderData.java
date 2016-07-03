package io.github.aweiland;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class ProviderData {

    @NotNull
    private String name;

    @NotNull
    private String authorizationUri;

    @NotNull
    private String accessTokenUri;

    private String apiUri;

    private String scopes;

    @JsonProperty
    public String getApiUri() {
        return apiUri;
    }

    @JsonProperty
    public void setApiUri(String apiUri) {
        this.apiUri = apiUri;
    }

    @JsonProperty
    public String getAuthorizationUri() {
        return authorizationUri;
    }

    @JsonProperty
    public void setAuthorizationUri(String authorizationUri) {
        this.authorizationUri = authorizationUri;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public String getScopes() {
        return scopes;
    }

    @JsonProperty
    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    @JsonProperty
    public String getAccessTokenUri() {
        return accessTokenUri;
    }

    @JsonProperty
    public void setAccessTokenUri(String accessTokenUri) {
        this.accessTokenUri = accessTokenUri;
    }
}
