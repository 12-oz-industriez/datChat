package datchat.handlers;

import datchat.model.chat.common.MessageWrapper;

public class Response<CR, BR> {
    private final MessageWrapper<CR> clientResponse;
    private final MessageWrapper<BR> broadcastResponse;

    public Response(MessageWrapper<CR> clientResponse, MessageWrapper<BR> broadcastResponse) {
        this.clientResponse = clientResponse;
        this.broadcastResponse = broadcastResponse;
    }

    public MessageWrapper<CR> getClientResponse() {
        return clientResponse;
    }

    public MessageWrapper<BR> getBroadcastResponse() {
        return broadcastResponse;
    }
}
