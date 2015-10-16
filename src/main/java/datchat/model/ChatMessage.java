package datchat.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import datchat.model.common.WebSocketMessage;
import org.bson.types.ObjectId;

public class ChatMessage extends WebSocketMessage {
    private final ObjectId chatMessageId;
    private final String body;
    private final String author;

    public ChatMessage(String id, ObjectId chatMessageId, String body, String author) {
        super(id);
        this.chatMessageId = chatMessageId;
        this.body = body;
        this.author = author;
    }

    @JsonCreator
    public ChatMessage(@JsonProperty("id") String id,
                       @JsonProperty("body") String body,
                       @JsonProperty("author") String author) {
        this(id, null, body, author);
    }

    public ObjectId getChatMessageId() {
        return chatMessageId;
    }

    public String getBody() {
        return body;
    }

    public String getAuthor() {
        return author;
    }

    public ChatMessage withChatMessageId(ObjectId chatMessageId) {
        return new ChatMessage(id, chatMessageId, body, author);
    }
}
