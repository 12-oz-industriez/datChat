package datchat.handlers.common;

import datchat.model.common.MessageType;
import datchat.model.common.MessageWrapper;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class MessageDispatcher {

    private final Map<MessageType, MessageHandler> handlers;

    @Inject
    public MessageDispatcher(List<MessageHandler<?>> handlers) {
        this.handlers = handlers.stream()
                .collect(HashMap::new,
                        (map, handler) -> map.put(handler.getMessageType(), handler),
                        HashMap::putAll);
    }

    public CompletableFuture<Response> dispatch(MessageWrapper message) {
        MessageType type = message.getType();
        MessageHandler<?> messageHandler = this.handlers.get(type);

        if (messageHandler == null) {
            throw new RuntimeException("No handler for " + type + " message type");
        }

        return messageHandler.handle(message);
    }
}
