package io.github.aweiland.oauth4j;



import io.github.aweiland.oauth4j.provider.OAuth1Provider;
import io.github.aweiland.oauth4j.provider.OAuth2Provider;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProviderRegistry {

    private final List<SocialProvider<?>> providers;

    public ProviderRegistry(List<SocialProvider<?>> providers) {
        this.providers = providers;
    }

    /**
     * Find a provider by name
     * @param id
     * @return
     */
    public Optional<SocialProvider<?>> get(final String id) {
        return providers.stream().filter(provider -> provider.getName().equals(id)).findFirst();
    }

    public List<SocialProvider<?>> getAll() {
        return this.providers;
    }

    public List<SocialProvider<?>> getOAuth2Providers() {
        return providers.stream()
                .filter(provider -> OAuth2Provider.class.isAssignableFrom(provider.getClass()))
                .collect(Collectors.toList());
    }

    public List<SocialProvider<?>> getOAuth1Providers() {
        return providers.stream()
                .filter(provider -> OAuth1Provider.class.isAssignableFrom(provider.getClass()))
                .collect(Collectors.toList());
    }
}
