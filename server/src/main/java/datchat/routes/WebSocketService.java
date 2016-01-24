package datchat.routes;

import datchat.handlers.common.CombinedResponse;
import datchat.handlers.common.MessageDispatcher;
import datchat.model.common.Request;
import datchat.model.common.Response;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.Json;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketService {
    private final Map<String, ServerWebSocket> activeConnections = new ConcurrentHashMap<>(512);

    private final MessageDispatcher messageDispatcher;

    @Inject
    public WebSocketService(MessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
    }

    public void handleNewConnection(ServerWebSocket webSocket) {
        activeConnections.put(webSocket.textHandlerID(), webSocket);

        webSocket.frameHandler(frame -> {
            String json = frame.textData();

            Request messageWrapper = Json.decodeValue(json, Request.class);

            CompletableFuture<CombinedResponse> responses = messageDispatcher.dispatch(messageWrapper);

            responses.thenAccept(response -> {
                Response<?> clientResponse = response.getClientResponse();
                Response<?> broadcastResponse = response.getBroadcastResponse();

                if (clientResponse != null) {
                    sendMessage(clientResponse, webSocket);
                }

                if (broadcastResponse != null) {
                    sendToAll(broadcastResponse, webSocket);
                }
            });
        });

        webSocket.closeHandler(v -> activeConnections.remove(webSocket.textHandlerID()));
    }

    public void sendMessage(Response<?> message, ServerWebSocket socket) {
        socket.writeFinalTextFrame(Json.encode(message));
    }

    public void sendToAll(Response<?> message, ServerWebSocket currentSocket) {
        activeConnections.values().stream()
                .filter(socket -> !socket.equals(currentSocket))
                .forEach(socket -> socket.writeFinalTextFrame(Json.encode(message)));
    }

}
