package io.github.aweiland.oauth4j.support;

/**
 * Holder for various oauth tokens
 */
public interface TokenHolder {

    /**
     * Get the main token
     * @return
     */
    String getToken();

    String getTokenType();

    /**
     * Get the secret token
     * @return
     */
    String getSecretToken();

    /**
     * Get the refresh token
     * @return
     */
    String getRefreshToken();
}
