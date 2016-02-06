package datchat.model.message;

import net.karneim.pojobuilder.GeneratePojoBuilder;
import org.bson.types.ObjectId;

public class ChatMessageResponse {
    private final ObjectId id;
    private final String body;
    private final String author;

    @GeneratePojoBuilder
    public ChatMessageResponse(ObjectId id, String body, String author) {
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

    public static datchat.model.message.ChatMessageResponseBuilder builder() {
        return new datchat.model.message.ChatMessageResponseBuilder();
    }
}
