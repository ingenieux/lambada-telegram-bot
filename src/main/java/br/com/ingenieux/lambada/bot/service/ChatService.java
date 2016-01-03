package br.com.ingenieux.lambada.bot.service;

import br.com.ingenieux.lambada.bot.model.ChatEvent;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class ChatService {
    @Inject
    ChatEventDao chatEventDao;

    public void truncateAllByChatId(Long chatId) {
        List<ChatEvent> eventsToDelete = new ArrayList<>(chatEventDao.findByChatId(chatId));

        chatEventDao.deleteAll(eventsToDelete);
    }
}
