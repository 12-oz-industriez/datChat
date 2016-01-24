package datchat.handlers.common;

import datchat.model.common.Response;

public class CombinedResponse {
    private final Response clientResponse;
    private final Response broadcastResponse;

    public CombinedResponse(Response clientResponse) {
        this(clientResponse, null);
    }

    public CombinedResponse(Response clientResponse, Response broadcastResponse) {
        this.clientResponse = clientResponse;
        this.broadcastResponse = broadcastResponse;
    }

    public Response getClientResponse() {
        return clientResponse;
    }

    public Response getBroadcastResponse() {
        return broadcastResponse;
    }
}
