package datchat.routes;

import datchat.handlers.MessageDispatcher;
import datchat.handlers.Response;
import datchat.model.chat.common.BaseMessage;
import datchat.model.chat.common.MessageWrapper;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.Json;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketService {
    private final Map<String, ServerWebSocket> activeConnections = new ConcurrentHashMap<>(512);

    private final MessageDispatcher messageDispatcher;

    public WebSocketService(MessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
    }

    public void handleNewConnection(ServerWebSocket webSocket) {
        activeConnections.put(webSocket.textHandlerID(), webSocket);

        webSocket.frameHandler(frame -> {
            String json = frame.textData();

            MessageWrapper messageWrapper = Json.decodeValue(json, MessageWrapper.class);

            CompletableFuture<Response<? extends BaseMessage, ? extends BaseMessage>> responses = messageDispatcher.dispatch(messageWrapper);

            responses.thenAccept(response -> {
                MessageWrapper<? extends BaseMessage> clientResponse = response.getClientResponse();
                MessageWrapper<? extends BaseMessage> broadcastResponse = response.getBroadcastResponse();

                if (clientResponse != null) {
                    sendMessage(clientResponse, webSocket);
                }

                if (broadcastResponse != null) {
                    sendToAll(broadcastResponse, webSocket);
                }
            });
        });

        webSocket.closeHandler(v -> activeConnections.remove(webSocket));
    }

    public void sendMessage(MessageWrapper<? extends BaseMessage> message, ServerWebSocket socket) {
        socket.writeFinalTextFrame(Json.encode(message));
    }

    public void sendToAll(MessageWrapper<? extends BaseMessage> message, ServerWebSocket currentSocket) {
        activeConnections.values().stream()
                .filter(socket -> !socket.equals(currentSocket))
                .forEach(socket -> socket.writeFinalTextFrame(Json.encode(message)));
    }

}
