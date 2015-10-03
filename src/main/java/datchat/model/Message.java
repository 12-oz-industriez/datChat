package datchat.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

public class Message {
    private final ObjectId id;
    private final String body;
    private final ObjectId author;

    public Message(ObjectId id, String body, ObjectId author) {
        this.id = id;
        this.body = body;
        this.author = author;
    }

    @JsonCreator
    public Message(@JsonProperty("body") String body) {
        this(null, body, null);
    }

    public ObjectId getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public ObjectId getAuthor() {
        return author;
    }
}
