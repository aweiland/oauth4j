package com.github.aweiland.oauth4j.provider.oauth1;

import com.github.aweiland.oauth4j.provider.OAuth1Provider;
import com.github.aweiland.oauth4j.provider.flow.AuthStart;
import com.github.aweiland.oauth4j.provider.flow.AuthVerify;
import com.github.aweiland.oauth4j.provider.flow.StartRequest;
import com.github.aweiland.oauth4j.support.OAuth1Info;
import com.github.aweiland.oauth4j.support.OAuthInfo;
import com.github.aweiland.oauth4j.support.ProviderDetails;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Optional;

public class TwitterProvider extends OAuth1Provider {

    private static final String DEFAULT_AUTH_URI = "https://twitter.com/oauth/request_token";
    private static final String DEFAULT_ACCESS_TOKEN_URI = "https://twitter.com/oauth/access_token";
    private static final String DEFAULT_API_URI = "https://api.twitter.com/1.1";

    public TwitterProvider(String appId, String appSecret) {
        super("twitter", "Twitter", appId, appSecret, DEFAULT_AUTH_URI, DEFAULT_ACCESS_TOKEN_URI, DEFAULT_API_URI);

    }

    @Override
    public Optional<AuthStart> start(StartRequest req) {
        try {
            Twitter twitter = createClient();
            RequestToken requestToken = twitter.getOAuthRequestToken(req.getReturnUri());

            return Optional.of(new AuthStart.Builder().redirectUri(requestToken.getAuthorizationURL()).requestToken(requestToken.getToken()).build());
        } catch (TwitterException e) {
            return Optional.empty();
        }

    }

    @Override
    public Optional<OAuthInfo> verify(AuthVerify req) {
        Twitter twitter = createClient(); // new TwitterFactory().getInstance();//FIXME createClient(req);
        try {
            String oauthVerifier = req.getCode();// getOauthVerifier();
            String oauthToken = req.getRequestToken();
            twitter.setOAuthAccessToken(new AccessToken(oauthToken, oauthVerifier));
            AccessToken accessToken = twitter.getOAuthAccessToken(oauthVerifier);

            return Optional.of(new OAuth1Info.Builder().provider("twitter")
                    .token(accessToken.getToken())
                    .secret(accessToken.getTokenSecret())
                    .build());
        } catch (TwitterException e) {
            // TODO Logger
            return Optional.empty();
        }

    }

    @Override
    public Optional<ProviderDetails> getDetails(OAuthInfo accessToken) {
        try {
            User user = createAuthedClient(accessToken.getToken(), accessToken.getSecretToken()).verifyCredentials();
            return Optional.of(new ProviderDetails.Builder()
                    .provider("twitter")
                    .providerId(Long.valueOf(user.getId())
                            .toString()).build());
        } catch (TwitterException e) {
            return Optional.empty();
        }
    }



    private Twitter createClient() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(getAppId()).setOAuthConsumerSecret(getAppSecret())
//            .setOAuthAuthorizationURL(getAuthUri())
                .setOAuthRequestTokenURL(getAuthUri())
            .setOAuthAccessTokenURL(getAccessTokenUri())
            .setRestBaseURL(getApiUri());

        TwitterFactory factory = new TwitterFactory(cb.build());
        return factory.getInstance();
    }

    private Twitter createAuthedClient(String token, String tokenSecret) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(getAppId()).setOAuthConsumerSecret(getAppSecret())
                .setOAuthAccessToken(token)
                .setOAuthAccessTokenSecret(tokenSecret)
                .setOAuthAuthorizationURL(getAuthUri())
                .setOAuthAccessTokenURL(getAccessTokenUri())
                .setRestBaseURL(getApiUri());

        TwitterFactory factory = new TwitterFactory(cb.build());
        return factory.getInstance();
    }

}
