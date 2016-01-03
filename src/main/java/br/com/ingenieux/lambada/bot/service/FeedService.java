package br.com.ingenieux.lambada.bot.service;

import br.com.ingenieux.lambada.bot.model.ChatEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nixtabyte.telegram.jtelebot.response.json.Update;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.defaultString;

public class FeedService {
    @Inject
    ChatEventDao chatEventDao;

    @Inject
    @Named("service.base.uri")
    String baseFeedUri;

    @Inject
    @Named("service.s3.uri")
    String baseS3Uri;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    AmazonS3 s3;

    public String getFeedUrlFor(String chatId) {
        return format(baseFeedUri, chatId);
    }

    public void generateFeedFor(Long chatId) throws Exception {
        Set<ChatEvent> entries = new TreeSet<>(chatEventDao.findByChatId(chatId));

        final Factory factory = new Abdera().getFactory();

        final Feed feed = factory.newFeed();

        for (ChatEvent chatEvent : entries) {
            Update update = objectMapper.readValue(chatEvent.getContent(), Update.class);

            if (defaultString(update.getMessage().getText()).startsWith("/")) {
                continue;
            }

            Entry newObj = factory.newEntry();

            newObj.setId("update:" + update.getUpdateId());
            newObj.setTitle(update.getMessage().getText());
            newObj.setPublished(new Date(1000 * update.getMessage().getUnixTimeDate()));
            newObj.setContent(update.getMessage().getText());

            feed.addEntry(newObj);
        }

        Document<Feed> doc = feed.getDocument();

        StringWriter stringWriter = new StringWriter();

        doc.writeTo(stringWriter);

        AmazonS3URI targetPath = new AmazonS3URI(format(baseS3Uri, "" + chatId));

        ObjectMetadata objectMetadata = new ObjectMetadata();

        objectMetadata.setContentType("application/atom+xml; charset=utf-8");

        PutObjectRequest putObjectRequest = new PutObjectRequest(targetPath.getBucket(), targetPath.getKey(), new ByteArrayInputStream(stringWriter.toString().getBytes()), objectMetadata);

        s3.putObject(putObjectRequest);
    }
}
