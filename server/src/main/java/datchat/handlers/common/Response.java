package datchat.handlers.common;

import datchat.model.common.MessageWrapper;

public class Response {
    private final MessageWrapper clientResponse;
    private final MessageWrapper broadcastResponse;

    public Response(MessageWrapper clientResponse) {
        this(clientResponse, null);
    }

    public Response(MessageWrapper clientResponse, MessageWrapper broadcastResponse) {
        this.clientResponse = clientResponse;
        this.broadcastResponse = broadcastResponse;
    }

    public MessageWrapper getClientResponse() {
        return clientResponse;
    }

    public MessageWrapper getBroadcastResponse() {
        return broadcastResponse;
    }
}
