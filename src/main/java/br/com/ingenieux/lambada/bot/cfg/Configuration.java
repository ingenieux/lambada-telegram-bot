package br.com.ingenieux.lambada.bot.cfg;

import javax.inject.Inject;
import javax.inject.Named;

public class Configuration {
    @Inject
    @Named("telegram.defaultEndpoint")
    String defaultEndpoint;

    public String getApiKey() {
        return apiKey;
    }

    @Inject
    @Named("telegram.apiKey")
    String apiKey;

    public String getDefaultEndpoint() {
        return defaultEndpoint;
    }
}
