package datchat.handlers.common;

import datchat.filters.common.MessageContext;
import datchat.model.common.MessageType;
import datchat.model.common.MessageWrapper;

import java.util.concurrent.CompletableFuture;

public interface MessageHandler<T> {
    CompletableFuture<Response> handle(MessageWrapper<T> message, MessageContext context);

    MessageType getMessageType();
}
