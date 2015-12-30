package io.ingenieux.lambada.bot;

import br.com.ingenieux.lambada.bot.LambadaTelegramBot;
import br.com.ingenieux.lambada.bot.di.CoreModule;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import io.github.nixtabyte.telegram.jtelebot.response.json.Update;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;

import java.util.Arrays;

public class LambdaProxy {
    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private Context context;

    @Inject
    private LambadaTelegramBot bot;

    private Vertx vertx;

    private HttpServer server;

    private Injector injector;

    public void execute() throws Exception {
        init();

        server.listen(8080);
    }

    private void init() {
        vertx = Vertx.vertx();

        server = vertx.createHttpServer();

        injector = Guice.createInjector(Arrays.asList(new CoreModule(), new ContextModule()));

        injector.injectMembers(this);

        server.requestHandler(event -> {
            event.bodyHandler(totalBuffer -> {
                final HttpServerResponse r = event.response().setChunked(true);

                try {
                    final String payloadAsString = totalBuffer.toString();

                    ObjectNode payload = (ObjectNode) objectMapper.readTree(payloadAsString);

                    Update update = objectMapper.convertValue(payload, Update.class);

                    /* Object objRresult = */ bot.onMessage(update, context);

                    /*
                    String result = null;

                    final HttpServerResponse r = event.response().putHeader("Content-Type", "application/json");

                    if (null != result) {
                        r.write(result);
                    }
                    */

                    r.putHeader("Content-Type", "application/json").write(payloadAsString).end();
                } catch (Exception exc) {
                    exc.printStackTrace();

                    final String excAsString = exc.toString();

                    r.putHeader("Content-Type", "text/plain").setStatusCode(500).write(excAsString).end();
                }
            });
        });
    }


    public static void main(String[] args) throws Exception {
        LambdaProxy proxy = new LambdaProxy();

        proxy.execute();

    }
}
