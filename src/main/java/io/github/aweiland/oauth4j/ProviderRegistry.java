package io.github.aweiland.oauth4j;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import io.github.aweiland.oauth4j.provider.OAuth1Provider;
import io.github.aweiland.oauth4j.provider.OAuth2Provider;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProviderRegistry {

    static {
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

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
