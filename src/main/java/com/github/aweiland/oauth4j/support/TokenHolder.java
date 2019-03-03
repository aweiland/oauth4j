package com.github.aweiland.oauth4j.support;

/**
 * Holder for various oauth tokens
 */
public interface TokenHolder {

    /**
     * Get the main token
     * @return
     */
    String getToken();

    /**
     * TODO What is this?
     * @return
     */
    String getTokenType();

    /**
     * Get the secret token
     * TODO What is this?
     * @return
     */
    String getSecretToken();

    /**
     * Get the refresh token
     * @return
     */
    String getRefreshToken();
}
