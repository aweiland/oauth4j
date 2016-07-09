package io.github.aweiland.oauth4j.provider.flow;

import io.github.aweiland.oauth4j.support.AppDataHolder;
import io.github.aweiland.oauth4j.support.ReturnUriHolder;

/**
 * Created by aweiland on 7/9/16.
 */
public class StartRequest implements AppDataHolder, ReturnUriHolder {

    @Override
    public String getAppId() {
        return null;
    }

    @Override
    public String getAppSecret() {
        return null;
    }

    @Override
    public String getReturnUri() {
        return null;
    }
}
