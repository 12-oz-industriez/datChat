package datchat.model.common;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import datchat.json.PayloadTypeResolver;

public abstract class BaseMessageWrapper<T, E> {
    private String id;
    private String sessionId;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @JsonTypeIdResolver(PayloadTypeResolver.class)
    private T payload;

    public BaseMessageWrapper() {
    }

    public BaseMessageWrapper(String id, String sessionId, T payload) {
        this.id = id;
        this.sessionId = sessionId;
        this.payload = payload;
    }

    public abstract E getType();

    public String getId() {
        return id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public T getPayload() {
        return payload;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
