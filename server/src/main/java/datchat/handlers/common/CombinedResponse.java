package datchat.handlers.common;

import datchat.model.common.Response;

import java.util.Optional;

public class CombinedResponse {
    private final Optional<Response> clientResponse;
    private final Optional<Response> broadcastResponse;

    public CombinedResponse(Response clientResponse) {
        this(clientResponse, null);
    }

    public CombinedResponse(Response clientResponse, Response broadcastResponse) {
        this.clientResponse = Optional.ofNullable(clientResponse);
        this.broadcastResponse = Optional.ofNullable(broadcastResponse);
    }

    public Optional<Response> getClientResponse() {
        return clientResponse;
    }

    public Optional<Response> getBroadcastResponse() {
        return broadcastResponse;
    }
}
