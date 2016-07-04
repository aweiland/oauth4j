package io.github.aweiland.oauth4j.provider.oauth1;

import io.github.aweiland.oauth4j.provider.OAuth1Provider;
import io.github.aweiland.oauth4j.provider.ProviderRequest;
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

    public TwitterProvider(String authUri, String atUri, String aUri) {
        super(authUri, atUri, aUri);
        this.setName("twitter");
    }


    @Override
    public Optional<ProviderRequest> start(ProviderRequest req) {
        try {
            Twitter twitter = createClient(req);
            RequestToken requestToken = twitter.getOAuthRequestToken(req.getFinishUri());
            req.setRedirectUri(requestToken.getAuthorizationURL());
            req.setRequestToken(requestToken.getToken());

            return Optional.of(req);
        } catch (TwitterException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<OAuth1Info> verify(ProviderRequest req) {
        Twitter twitter = createClient(req);
        try {
            String oauthVerifier = req.getOauthVerifier();
            String oauthToken = req.getOauthToken();
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
    public Optional<ProviderDetails> getProviderDetails(ProviderRequest req, String token, String tokenSecret) {
        try {
            User user = createAuthedClient(req, token, tokenSecret).verifyCredentials();
            return Optional.of(new ProviderDetails.Builder().provider("twitter").providerId(Long.valueOf(user.getId()).toString()).build());
        } catch (TwitterException e) {
            return Optional.empty();
        }
    }

    private Twitter createClient(ProviderRequest req) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(req.getKey()).setOAuthConsumerSecret(req.getSecret());

        TwitterFactory factory = new TwitterFactory(cb.build());
        return factory.getInstance();
    }

    private Twitter createAuthedClient(ProviderRequest req, String token, String tokenSecret) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(req.getKey()).setOAuthConsumerSecret(req.getSecret())
                .setOAuthAccessToken(token)
                .setOAuthAccessTokenSecret(tokenSecret);

        TwitterFactory factory = new TwitterFactory(cb.build());
        return factory.getInstance();
    }

}
