package io.ingenieux.lambada.bot;

import br.com.ingenieux.lambada.bot.di.CoreModule;
import br.com.ingenieux.lambada.bot.cfg.Configuration;
import com.google.inject.Guice;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class InjectionConfigTest {
    @Before
    public void setup() {
        Guice.createInjector(Collections.singletonList(new CoreModule())).injectMembers(this);
    }

    @Inject
    Configuration cfg;

    @Test
    public void testNonEmptyProperties() {
        assertThat("cfg", cfg, is(notNullValue()));

        assertThat("cfg.apiKey", cfg.getApiKey(), is(not(equalTo(""))));

        assertThat("cfg.defaultEndpoint", cfg.getDefaultEndpoint(), is(not(equalTo(""))));
    }
}
