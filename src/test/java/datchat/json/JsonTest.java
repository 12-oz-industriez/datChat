package datchat.json;

import datchat.config.RootConfig;
import datchat.model.chat.common.MessageType;
import datchat.model.chat.common.MessageWrapper;
import datchat.model.chat.message.ChatMessage;
import io.vertx.core.json.Json;
import org.bson.types.ObjectId;
import org.junit.Test;

public class JsonTest {

    @Test
    public void test() {
        RootConfig rootConfig = new RootConfig();
        rootConfig.objectMapper();

        MessageWrapper message = new MessageWrapper<>(
                "id", MessageType.NEW_MESSAGE, new ChatMessage(new ObjectId(), "body", "author")
        );

        String encoded = Json.encode(message);
        System.out.println(Json.decodeValue(encoded, MessageWrapper.class));
    }
}
