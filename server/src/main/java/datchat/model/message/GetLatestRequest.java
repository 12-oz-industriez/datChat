package datchat.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import datchat.model.annotation.PayloadSubType;
import datchat.model.common.RequestMessageType;
import org.bson.types.ObjectId;

@PayloadSubType(RequestMessageType.GET_LATEST)
public class GetLatestRequest {
    private final ObjectId lastMessageId;
    private final Integer count;

    public GetLatestRequest(@JsonProperty("lastMessageId") ObjectId lastMessageId,
                            @JsonProperty("count") Integer count) {
        this.lastMessageId = lastMessageId;
        this.count = count;
    }

    public ObjectId getLastMessageId() {
        return lastMessageId;
    }

    public Integer getCount() {
        return count;
    }
}
