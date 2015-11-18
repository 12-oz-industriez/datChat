package datchat.handlers;

import datchat.model.chat.common.BaseMessage;
import datchat.model.chat.common.MessageWrapper;

public class Response<T extends BaseMessage, U extends BaseMessage> {
    private final MessageWrapper<T> clientResponse;
    private final MessageWrapper<U> broadcastResponse;

    public Response(MessageWrapper<T> clientResponse, MessageWrapper<U> broadcastResponse) {
        this.clientResponse = clientResponse;
        this.broadcastResponse = broadcastResponse;
    }

    public MessageWrapper<T> getClientResponse() {
        return clientResponse;
    }

    public MessageWrapper<U> getBroadcastResponse() {
        return broadcastResponse;
    }
}
