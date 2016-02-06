package datchat.routes;

import com.google.common.base.Stopwatch;
import datchat.handlers.common.CombinedResponse;
import datchat.handlers.common.MessageDispatcher;
import datchat.model.common.Request;
import datchat.model.common.Response;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rx.Observable;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketService.class);

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

            Stopwatch stopwatch = Stopwatch.createStarted();
            Observable<CombinedResponse> responses = messageDispatcher.dispatch(messageWrapper);

            LOGGER.info("Request handling took {}", stopwatch);

            responses.subscribe(combinedResponse -> {
                combinedResponse.getClientResponse().ifPresent(r -> sendMessage(r, webSocket));
                combinedResponse.getBroadcastResponse().ifPresent(r -> sendBroadcastMessage(r, webSocket));
            });
        });

        webSocket.closeHandler(v -> activeConnections.remove(webSocket.textHandlerID()));
    }

    public void sendMessage(Response<?> message, ServerWebSocket socket) {
        socket.writeFinalTextFrame(Json.encode(message));
    }

    public void sendBroadcastMessage(Response<?> message, ServerWebSocket currentSocket) {
        activeConnections.values().stream()
                .filter(socket -> !socket.equals(currentSocket))
                .forEach(socket -> socket.writeFinalTextFrame(Json.encode(message)));
    }

}
