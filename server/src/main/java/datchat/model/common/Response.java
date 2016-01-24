package datchat.model.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class Response<T> extends BaseMessageWrapper<T, ResponseMessageType> {

    private final ResponseMessageType type;

    public Response(ResponseMessageType type, T payload) {
        super(UUID.randomUUID().toString(), null, payload);
        this.type = type;
    }

    public Response(String id, ResponseMessageType type, T payload) {
        super(id, null, payload);
        this.type = type;
    }

    @JsonCreator
    public Response(@JsonProperty("id") String id,
                    @JsonProperty("sessionId") String sessionId,
                    @JsonProperty("type") ResponseMessageType type,
                    @JsonProperty("payload") T payload) {
        super(id, sessionId, payload);
        this.type = type;
    }

    @Override
    public ResponseMessageType getType() {
        return type;
    }

}
