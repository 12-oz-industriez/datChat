package datchat.handlers.common;

import datchat.exception.ExceptionHandler;
import datchat.filters.common.MessageContext;
import datchat.filters.common.MessageFilter;
import datchat.model.common.MessageType;
import datchat.model.common.MessageWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MessageDispatcher {

    private final Map<MessageType, MessageHandler> handlers;

    private final Map<MessageType, List<MessageFilter>> messageFilters;

    private final ExceptionHandler exceptionHandler;

    public MessageDispatcher(List<MessageHandler<?>> handlers,
                             Map<MessageType, List<MessageFilter>> messageFilters,
                             ExceptionHandler exceptionHandler) {
        this.handlers = handlers.stream()
                .collect(HashMap::new,
                        (map, handler) -> map.put(handler.getMessageType(), handler),
                        HashMap::putAll);

        this.messageFilters = messageFilters;
        this.exceptionHandler = exceptionHandler;
    }

    public CompletableFuture<Response> dispatch(MessageWrapper message) {
        MessageType type = message.getType();
        MessageHandler<?> messageHandler = this.handlers.get(type);

        if (messageHandler == null) {
            throw new RuntimeException("No handler for " + type + " message type");
        }

        MessageContext messageContext = new MessageContext();
        for (MessageFilter messageFilter : messageFilters.get(type)) {
            try {
                message = messageFilter.filter(message, messageContext);
            } catch (Exception e) {
                return CompletableFuture.completedFuture(this.exceptionHandler.handleThrowable(e));
            }
        }

        CompletableFuture<Response> future = messageHandler.handle(message, messageContext);
        return future.exceptionally(this.exceptionHandler::handleThrowable);
    }
}
