package datchat.model.chat.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import datchat.json.PayloadTypeResolver;

import java.util.UUID;

public class MessageWrapper<T> {
    private final String id;
    private final MessageType type;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @JsonTypeIdResolver(PayloadTypeResolver.class)
    private final T payload;

    public MessageWrapper(MessageType type, T payload) {
        this(UUID.randomUUID().toString(), type, payload);
    }

    @JsonCreator
    public MessageWrapper(@JsonProperty("id") String id,
                          @JsonProperty("type") MessageType type,
                          @JsonProperty("payload") T payload) {
        this.id = id;
        this.type = type;
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public MessageType getType() {
        return type;
    }

    public T getPayload() {
        return payload;
    }
}
