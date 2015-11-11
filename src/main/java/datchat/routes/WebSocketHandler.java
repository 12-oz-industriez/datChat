package datchat.routes;

import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class WebSocketHandler implements Handler<ServerWebSocket> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketHandler.class);

    private final String path;

    private final WebSocketService webSocketService;

    public WebSocketHandler(String path, WebSocketService webSocketService) {
        this.path = path;
        this.webSocketService = webSocketService;
    }

    @Override
    public void handle(ServerWebSocket socket) {
        try {
            if (!Objects.equals(socket.path(), this.path)) {
                socket.reject();
                return;
            }

            webSocketService.handleNewConnection(socket);
        } catch (Exception e) {
            LOGGER.error("{}", e);
        }
    }
}
