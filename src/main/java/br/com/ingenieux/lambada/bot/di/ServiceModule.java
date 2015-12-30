package br.com.ingenieux.lambada.bot.di;

import br.com.ingenieux.lambada.bot.cfg.Configuration;
import br.com.ingenieux.lambada.bot.service.ChatEventDao;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.nixtabyte.telegram.jtelebot.client.RequestHandler;
import io.github.nixtabyte.telegram.jtelebot.client.impl.DefaultRequestHandler;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

public class ServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Configuration.class).in(Singleton.class);
        bind(ChatEventDao.class).in(Singleton.class);
    }

    @Inject
    @Provides
    @Singleton
    public RequestHandler getRequestHandler(@Named("telegram.apiKey") String apiToken, ObjectMapper objectMapper) {
        final DefaultRequestHandler defaultRequestHandler = new DefaultRequestHandler(apiToken);

        defaultRequestHandler.setObjectMapper(objectMapper);

        return defaultRequestHandler;
    }

    @Inject
    @Provides
    @Singleton
    public AmazonDynamoDB getAmazonDynamoDB() {
        return new AmazonDynamoDBClient();
    }

    @Inject
    @Provides
    @Singleton
    @Named("chat_event")
    public DynamoDBMapper getDynamoDBMapper(AmazonDynamoDB dynamoDB, @Named("telegram.bot.event_table") String eventTableName) {
        DynamoDBMapperConfig mapperConfig = new DynamoDBMapperConfig(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(eventTableName));

        DynamoDBMapper ddbMapper = new DynamoDBMapper(dynamoDB, mapperConfig);

        return ddbMapper;
    }
}
