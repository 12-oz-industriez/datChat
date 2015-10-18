package datchat.routes;

import datchat.dao.MessageDao;
import datchat.model.chat.common.BaseMessage;
import datchat.model.chat.common.MessageType;
import datchat.model.chat.common.MessageWrapper;
import datchat.model.chat.message.ChatMessage;
import datchat.model.chat.save.SaveResult;
import datchat.model.chat.save.SaveStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

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

                LOGGER.debug("Websocket message: {}", json);

                @SuppressWarnings("unchecked")
                MessageWrapper<ChatMessage> messageWrapper = Json.decodeValue(json, MessageWrapper.class);

                String id = messageWrapper.getId();

                messageDao.save(messageWrapper.getPayload())
                        .thenAccept(message -> {
                            sendMessage(MessageType.SAVE_RESULT, new SaveResult(id, SaveStatus.OK), socket);
                            sendToAllActiveSockets(MessageType.NEW_MESSAGE, message, socket);
                        })
                        .exceptionally(t -> {
                            sendMessage(MessageType.SAVE_RESULT, new SaveResult(id, SaveStatus.ERROR), socket);

                            return null;
                        });
            });

            socket.closeHandler((v) -> activeConnections.remove(socket));
        } catch (Exception e) {
            LOGGER.error("{}", e);
        }
    }

    private void sendMessage(MessageType type, BaseMessage payload, ServerWebSocket currentSocket) {
        long time = new Date().getTime();
        MessageWrapper<BaseMessage> wrapper = new MessageWrapper<>(String.valueOf(time), type, payload);

        currentSocket.writeFinalTextFrame(Json.encode(wrapper));
    }

    private void sendToAllActiveSockets(MessageType type, BaseMessage payload, ServerWebSocket currentSocket) {
        long time = new Date().getTime();
        MessageWrapper<BaseMessage> wrapper = new MessageWrapper<>(String.valueOf(time), type, payload);

        activeConnections.stream()
                .filter(socket -> !socket.equals(currentSocket))
                .forEach(socket -> socket.writeFinalTextFrame(Json.encode(wrapper)));
    }
}
