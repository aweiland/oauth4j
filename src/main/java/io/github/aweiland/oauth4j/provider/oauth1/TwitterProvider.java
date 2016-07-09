package io.github.aweiland.oauth4j.provider.oauth1;

import io.github.aweiland.oauth4j.provider.OAuth1Provider;
import io.github.aweiland.oauth4j.provider.flow.AuthStart;
import io.github.aweiland.oauth4j.provider.flow.AuthVerify;
import io.github.aweiland.oauth4j.provider.flow.StartRequest;
import io.github.aweiland.oauth4j.support.AppDataHolder;
import io.github.aweiland.oauth4j.support.OAuth1Info;
import io.github.aweiland.oauth4j.support.ProviderDetails;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Optional;

public class TwitterProvider extends OAuth1Provider {

    private static final String AUTH_URI = "https://twitter.com/oauth/request_token";
    private static final String ACCESS_TOKEN_URI = "https://twitter.com/oauth/access_token";

    public TwitterProvider(String appId, String appSecret) {
        super(appId, appSecret);
        this.setName("twitter");
    }

    @Override
    public Optional<AuthStart> start(StartRequest req) {
        try {
            Twitter twitter = createClient(req);
            RequestToken requestToken = twitter.getOAuthRequestToken(req.getReturnUri());

            return Optional.of(new AuthStart.Builder().redirectUri(requestToken.getAuthorizationURL()).requestToken(requestToken.getToken()).build());
        } catch (TwitterException e) {
            return Optional.empty();
        }

    }



    @Override
    public Optional<OAuth1Info> verify(AuthVerify req) {
        Twitter twitter = createClient(req); // new TwitterFactory().getInstance();//FIXME createClient(req);
        try {
            String oauthVerifier = req.getCode();// getOauthVerifier();
            String oauthToken = req.getRequestToken();
            twitter.setOAuthAccessToken(new AccessToken(oauthToken, oauthVerifier));
//            AccessToken accessToken = twitter.getOAuthAccessToken(req.getRequestToken(), oauthVerifier);
            AccessToken accessToken = twitter.getOAuthAccessToken(oauthVerifier);
            return Optional.of(new OAuth1Info.Builder().provider("twitter")
                    .identifier(Long.valueOf(accessToken.getUserId()).toString())
                    .token(accessToken.getToken())
                    .secret(accessToken.getTokenSecret())
                    .build());
        } catch (TwitterException e) {
            return Optional.empty();
        }

    }

    @Override
    public Optional<ProviderDetails> getProviderDetails(AppDataHolder appData, String token, String tokenSecret) {
        try {
            User user = createAuthedClient(appData, token, tokenSecret).verifyCredentials();
            return Optional.of(new ProviderDetails.Builder().provider("twitter").providerId(Long.valueOf(user.getId()).toString()).build());
        } catch (TwitterException e) {
            return Optional.empty();
        }
    }

    @Override
    protected String getAuthUri() {
        return AUTH_URI;
    }

    @Override
    protected String getAccessTokenUri() {
        return ACCESS_TOKEN_URI;
    }

    private Twitter createClient(AppDataHolder req) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(getAppId()).setOAuthConsumerSecret(getAppSecret());

        TwitterFactory factory = new TwitterFactory(cb.build());
        return factory.getInstance();
    }

    private Twitter createAuthedClient(AppDataHolder req, String token, String tokenSecret) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(getAppId()).setOAuthConsumerSecret(getAppSecret())
                .setOAuthAccessToken(token)
                .setOAuthAccessTokenSecret(tokenSecret);

        TwitterFactory factory = new TwitterFactory(cb.build());
        return factory.getInstance();
    }

}
