package io.github.aweiland;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import java.util.Optional;

public class SocialModule extends AbstractModule {

    private Optional<ProviderRegistry> registry = Optional.empty();

    @Override
    protected void configure() {

    }

    @Provides
    @Named("providerRegistry")
    public ProviderRegistry provideProviderRegistry(@Named("providerRegistryFactory") ProviderRegistryFactory factory) {
        if (!registry.isPresent()) {
            registry = Optional.of(factory.build());
        }

        return registry.get();
    }

}
