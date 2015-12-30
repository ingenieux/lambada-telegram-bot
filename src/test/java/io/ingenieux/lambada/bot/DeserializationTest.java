package io.ingenieux.lambada.bot;

import br.com.ingenieux.lambada.bot.di.CoreModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import io.github.nixtabyte.telegram.jtelebot.response.json.Update;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class DeserializationTest {
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        this.objectMapper = Guice.createInjector(new CoreModule()).getInstance(ObjectMapper.class);
    }

    @Test
    public void testDeserialization() throws Exception {
        String payload = "{\n" +
                "    \"update_id\": 255421622,\n" +
                "    \"message\": {\n" +
                "        \"message_id\": 5,\n" +
                "        \"from\": {\n" +
                "            \"id\": 116644997,\n" +
                "            \"first_name\": \"Aldrin\",\n" +
                "            \"last_name\": \"Leal\",\n" +
                "            \"username\": \"aldrinleal\"\n" +
                "        },\n" +
                "        \"chat\": {\n" +
                "            \"id\": 116644997,\n" +
                "            \"first_name\": \"Aldrin\",\n" +
                "            \"last_name\": \"Leal\",\n" +
                "            \"username\": \"aldrinleal\",\n" +
                "            \"type\": \"private\"\n" +
                "        },\n" +
                "        \"date\": 1451496874,\n" +
                "        \"text\": \"/start\"\n" +
                "    }\n" +
                "}";

        Update update = objectMapper.readValue(payload, Update.class);

        assertThat("update", update, is(notNullValue()));
    }
}
