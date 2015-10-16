package datchat.routes;

import datchat.dao.MessageDao;
import datchat.model.ChatMessage;
import datchat.model.common.SaveResult;
import datchat.model.common.SaveStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class WebSocketHandler implements Handler<ServerWebSocket> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketHandler.class);

    private final String path;
    private final MessageDao messageDao;

    private final Set<ServerWebSocket> activeConnections = new ConcurrentHashSet<>();

    public WebSocketHandler(String path,
                            MessageDao messageDao) {
        this.path = path;
        this.messageDao = messageDao;
    }

    @Override
    public void handle(ServerWebSocket socket) {
        try {
            if (!Objects.equals(socket.path(), this.path)) {
                socket.reject();
                return;
            }

            activeConnections.add(socket);

            socket.frameHandler(frame -> {
                String json = frame.textData();
                ChatMessage chatMessage = Json.decodeValue(json, ChatMessage.class);

                String messageId = chatMessage.getId();

                messageDao.save(
                        chatMessage,
                        saveMessageCallback(socket),
                        (exc) -> socket.writeFinalTextFrame(Json.encode(new SaveResult(messageId, SaveStatus.ERROR)))
                );
            });

            socket.closeHandler((v) -> activeConnections.remove(socket));
        } catch (Exception e) {
            LOGGER.error("{}", e);
        }
    }

    private Consumer<ChatMessage> saveMessageCallback(ServerWebSocket socket) {
        return (chatMessage) -> {
            String id = chatMessage.getId();

            socket.writeFinalTextFrame(Json.encode(new SaveResult(id, SaveStatus.OK)));

            activeConnections.stream()
                    .filter(activeSocket -> !activeSocket.equals(socket))
                    .forEach(activeSocket -> activeSocket.writeFinalTextFrame(Json.encode(chatMessage)));
        };
    }
}
