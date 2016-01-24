package datchat.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import datchat.model.annotation.PayloadSubType;
import datchat.model.common.RequestMessageType;
import net.karneim.pojobuilder.GeneratePojoBuilder;
import org.bson.types.ObjectId;

@PayloadSubType(RequestMessageType.NEW_MESSAGE)
public class ChatMessage {
    private final ObjectId id;
    private final String body;
    private final String author;

    @GeneratePojoBuilder
    @JsonCreator
    public ChatMessage(@JsonProperty("id") ObjectId id,
                       @JsonProperty("body") String body,
                       @JsonProperty("author") String author) {
        this.id = id;
        this.body = body;
        this.author = author;
    }

    public ObjectId getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public String getAuthor() {
        return author;
    }

    public ChatMessageBuilder toBuilder() {
        return new ChatMessageBuilder()
                .withId(this.id)
                .withBody(this.body)
                .withAuthor(this.author);
    }

    public static ChatMessageBuilder builder() {
        return new ChatMessageBuilder();
    }
}
