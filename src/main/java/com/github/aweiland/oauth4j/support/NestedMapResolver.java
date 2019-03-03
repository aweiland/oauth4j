package com.github.aweiland.oauth4j.support;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Created by aweiland on 2/9/16.
 */
public class NestedMapResolver {

    public static <T> Optional<T> resolve(Supplier<T> resolver) {
        try {
            T result = resolver.get();
            return Optional.ofNullable(result);
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }
}
