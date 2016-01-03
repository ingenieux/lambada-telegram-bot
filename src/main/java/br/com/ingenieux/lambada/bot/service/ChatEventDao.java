package br.com.ingenieux.lambada.bot.service;

import br.com.ingenieux.lambada.bot.model.ChatEvent;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.google.inject.Inject;

import javax.inject.Named;
import javax.inject.Qualifier;
import java.util.Collection;

public class ChatEventDao {
    @Inject
    @Named("chat_event")
    DynamoDBMapper ddbMapper;

    public PaginatedQueryList<ChatEvent> findByChatId(Long chatId) {
        DynamoDBQueryExpression<ChatEvent> queryExpression = new DynamoDBQueryExpression<>();

        ChatEvent chatEventKey = new ChatEvent();

        chatEventKey.setChatId(chatId);

        queryExpression.withHashKeyValues(chatEventKey);

        return ddbMapper.query(ChatEvent.class, queryExpression);
    }

    public void save(ChatEvent chatEvent) {
        ddbMapper.save(chatEvent);
    }
}
