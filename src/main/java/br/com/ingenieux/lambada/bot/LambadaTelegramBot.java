package br.com.ingenieux.lambada.bot;

import br.com.ingenieux.lambada.bot.cfg.Configuration;
import br.com.ingenieux.lambada.bot.di.CoreModule;
import br.com.ingenieux.lambada.bot.model.ChatEvent;
import br.com.ingenieux.lambada.bot.service.ChatEventDao;
import br.com.ingenieux.lambada.bot.service.FeedService;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.github.nixtabyte.telegram.jtelebot.client.RequestHandler;
import io.github.nixtabyte.telegram.jtelebot.request.TelegramRequest;
import io.github.nixtabyte.telegram.jtelebot.request.factory.TelegramRequestFactory;
import io.github.nixtabyte.telegram.jtelebot.response.json.TelegramResponse;
import io.github.nixtabyte.telegram.jtelebot.response.json.Update;
import io.ingenieux.lambada.runtime.LambadaFunction;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.Arrays;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

public class LambadaTelegramBot {
    public LambadaTelegramBot() {
        this(Arrays.asList(new CoreModule()));
    }

    public LambadaTelegramBot(Iterable<Module> modules) {
        Injector injector = Guice.createInjector(modules);

        injector.injectMembers(this);
    }

    @Inject
    Configuration cfg;

    @Inject
    RequestHandler rh;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    ChatEventDao chatEventDao;

    @Inject
    FeedService feedService;

    @LambadaFunction(name = "loggerbot_register", memorySize = 256, timeout = 60)
    public String registerBot(String url, Context ctx) throws Exception {
        String urlToRegister = defaultIfEmpty(url, cfg.getDefaultEndpoint());

        final TelegramRequest setWebhookRequest = TelegramRequestFactory.createSetWebhookRequest(urlToRegister);

        final TelegramResponse<?> telegramResponse = rh.sendRequest(setWebhookRequest);

        ctx.getLogger().log(format("Setting bot with token '%s' to webhook url: '%s'", cfg.getApiKey(), urlToRegister));
        ctx.getLogger().log(format("Response: %s", telegramResponse));

        return urlToRegister;
    }

    @LambadaFunction(name = "loggerbot_message", memorySize = 512, timeout = 300)
    public void onMessage(InputStream is, Context ctx) throws Exception {
        String updateAsString = IOUtils.toString(is);

        Update update = objectMapper.readValue(updateAsString, Update.class);

        ChatEvent chatEvent = new ChatEvent();

        chatEvent.setChatId(update.getMessage().getChat().getId());
        chatEvent.setUpdateId(update.getUpdateId());
        chatEvent.setContent(updateAsString);

        chatEventDao.save(chatEvent);

        if ("/start".equals(update.getMessage().getText())) {
            String feedUri = feedService.getFeedUrlFor("" + update.getMessage().getChat().getId());

            final TelegramRequest sendMessageRequest = TelegramRequestFactory.createSendMessageRequest(update.getMessage().getChat().getId(), "Ola, seus porra! O que disseres será guardado em " + feedUri, false, null, null);

            final TelegramResponse<?> resp = rh.sendRequest(sendMessageRequest);

            ctx.getLogger().log("Response: " + resp);
        } else if (! update.getMessage().getText().startsWith("/")) {
            feedService.generateFeedFor(update.getMessage().getChat().getId());
        }
    }
}
