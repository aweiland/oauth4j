package io.github.aweiland.oauth4j.provider.oauth2;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import io.github.aweiland.oauth4j.provider.OAuth2Provider;
import io.github.aweiland.oauth4j.provider.flow.AuthStart;
import io.github.aweiland.oauth4j.provider.flow.AuthVerify;
import io.github.aweiland.oauth4j.provider.flow.StartRequest;
import io.github.aweiland.oauth4j.support.AppDataHolder;
import io.github.aweiland.oauth4j.support.OAuth2Info;
import io.github.aweiland.oauth4j.support.OAuthInfo;
import io.github.aweiland.oauth4j.support.ProviderDetails;
import io.github.aweiland.oauth4j.support.ReturnUriHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Optional;

public class DropboxProvider extends OAuth2Provider {
    Logger LOGGER = LoggerFactory.getLogger(DropboxProvider.class);

    private static final String DEFAULT_AUTH_URI = "https://www.dropbox.com/1/oauth2/authorize";
    private static final String DEFAULT_ACCESS_TOKEN_URI = "https://api.dropbox.com/1/oauth2/token";
    private static final String DEFAULT_API_URI = "http://api.dropboxapi.com/1/account/info";



    public DropboxProvider(String appId, String appSecret) {
        super("dropbox", "Dropbox", appId, appSecret, DEFAULT_AUTH_URI, DEFAULT_ACCESS_TOKEN_URI, DEFAULT_API_URI);
    }

    @Override
    public Optional<AuthStart> start(StartRequest req) {
        return Optional.of(new AuthStart.Builder().redirectUri(getRedirectUri(req)).build());
    }

    @Override
    public Optional<OAuthInfo> verify(AuthVerify req) {
        Optional<String> code = Optional.ofNullable(req.getCode());
        return code.map(s -> performCodeExchange(s, req)).orElse(Optional.empty());
    }

    @Override
    public Optional<ProviderDetails> getDetails(OAuthInfo accessToken) {
        try {
            final HttpResponse<DropboxDetails> response = Unirest.get(this.getApiUri()).queryString("access_token", accessToken)
                    .asObject(DropboxDetails.class);
//            final DropboxDetails details = target.request(MediaType.APPLICATION_JSON_TYPE).get(DropboxDetails.class);
            DropboxDetails details = response.getBody();
            return Optional.of(new ProviderDetails.Builder()
                    .provider("dropbox")
                    .providerId(details.uid)
                    .displayName(details.displayName)
                    .build());
        } catch (Exception e) {
            return Optional.empty();
        }
    }


    private <T extends AppDataHolder & ReturnUriHolder> String getRedirectUri(T req) {
        return Unirest.get(getAuthUri())
                .queryString("client_id", this.getAppId())
                .queryString("redirect_uri", req.getReturnUri())
                .queryString("response_type", "code")
                .getUrl();
//        return UriBuilder.fromUri(getAuthUri())
//                .queryParam("client_id", getAppId())
//                .queryParam("redirect_uri", req.getReturnUri())
//                .queryParam("response_type", "code")
//                .build().toString();
    }


    public static class DropboxDetails {

        String uid;

        String displayName;

    }

}
