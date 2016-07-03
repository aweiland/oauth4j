package io.github.aweiland;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.icyjupiter.api.ProviderData;
import com.icyjupiter.api.auth.social.provider.oauth1.TwitterProvider;
import com.icyjupiter.api.auth.social.provider.oauth2.DropboxProvider;
import com.icyjupiter.api.auth.social.provider.oauth2.FacebookProvider;
import com.icyjupiter.api.auth.social.provider.oauth2.GoogleProvider;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by aweiland on 5/14/16.
 */
public class ProviderRegistryFactory {

    @NotNull
    private List<ProviderData> socialProviders = Collections.emptyList();

    public ProviderRegistry build() {
        final List<SocialProvider<?>> collect = socialProviders.stream()
                .map(this::createProvider)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        return new ProviderRegistry(collect);
    }

    private Optional<SocialProvider<?>> createProvider(ProviderData data) {
        switch (data.getName()) {
            case "facebook": return Optional.of(new FacebookProvider(data.getAuthorizationUri(), data.getAccessTokenUri(), data.getApiUri()));
            case "twitter" : return Optional.of(new TwitterProvider(data.getAuthorizationUri(), data.getAccessTokenUri(), data.getApiUri()));
            case "google" : return Optional.of(new GoogleProvider(data.getAuthorizationUri(), data.getAccessTokenUri(), data.getApiUri(), data.getScopes()));
            case "dropbox" : return Optional.of(new DropboxProvider(data.getAuthorizationUri(), data.getAccessTokenUri(), data.getApiUri()));
        }

        return Optional.empty();
    }

    @JsonProperty("socialProviders")
    public void setSocialProviders(List<ProviderData> socialProviders) {
        this.socialProviders = socialProviders;
    }

    @JsonProperty("socialProviders")
    public List<ProviderData> getSocialProviders() {
        return this.socialProviders;
    }
}