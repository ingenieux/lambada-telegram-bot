package br.com.ingenieux.lambada.bot.di;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Provides;
import org.nnsoft.guice.rocoto.configuration.ConfigurationModule;

import javax.inject.Singleton;
import java.net.URI;

public class CoreModule extends ConfigurationModule {
    @Override
    protected void bindConfigurations() {
        bindProperties(URI.create("classpath:/runtime-config.properties"));

        install(new ServiceModule());

    }

    @Provides
    @Singleton
    public ObjectMapper getObjectMapper() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enableDefaultTyping();

        return objectMapper;

    }
}
