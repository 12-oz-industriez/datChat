package datchat.handlers;

import datchat.model.chat.common.BaseMessage;
import datchat.model.chat.common.MessageType;
import datchat.model.chat.common.MessageWrapper;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MessageDispatcher {

    private final Map<MessageType, MessageHandler> handlers;

    public MessageDispatcher(Map<MessageType, MessageHandler> handlers) {
        this.handlers = handlers;
    }

    public CompletableFuture<Response<? extends BaseMessage, ? extends BaseMessage>> dispatch(MessageWrapper message) {
        MessageType type = message.getType();
        MessageHandler messageHandler = this.handlers.get(type);

        if (messageHandler == null) {
            throw new RuntimeException("No handler for " + type + " message type");
        }

        return messageHandler.handle(message);
    }
}
