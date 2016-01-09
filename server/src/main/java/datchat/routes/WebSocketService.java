package datchat.routes;

import datchat.handlers.common.MessageDispatcher;
import datchat.handlers.common.Response;
import datchat.model.chat.common.MessageWrapper;
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

            MessageWrapper messageWrapper = Json.decodeValue(json, MessageWrapper.class);

            CompletableFuture<Response> responses = messageDispatcher.dispatch(messageWrapper);

            responses.thenAccept(response -> {
                MessageWrapper<?> clientResponse = response.getClientResponse();
                MessageWrapper<?> broadcastResponse = response.getBroadcastResponse();

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

    public void sendMessage(MessageWrapper<?> message, ServerWebSocket socket) {
        socket.writeFinalTextFrame(Json.encode(message));
    }

    public void sendToAll(MessageWrapper<?> message, ServerWebSocket currentSocket) {
        activeConnections.values().stream()
                .filter(socket -> !socket.equals(currentSocket))
                .forEach(socket -> socket.writeFinalTextFrame(Json.encode(message)));
    }

}
