package datchat.model.common;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import datchat.json.PayloadTypeResolver;

import java.util.UUID;

public class MessageWrapper<T> {
    private String id;
    private MessageType type;

    private String sessionId;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @JsonTypeIdResolver(PayloadTypeResolver.class)
    private T payload;

    public MessageWrapper() {
    }

    public MessageWrapper(ResponseMessageType type, T payload) {
        this(UUID.randomUUID().toString(), type, payload);
    }

    public MessageWrapper(String id, ResponseMessageType type, T payload) {
        this.id = id;
        this.type = type;
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(ResponseMessageType type) {
        this.type = type;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
