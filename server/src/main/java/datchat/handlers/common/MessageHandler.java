package datchat.handlers.common;

import datchat.model.chat.common.MessageType;
import datchat.model.chat.common.MessageWrapper;

import java.util.concurrent.CompletableFuture;

public interface MessageHandler<T> {
    CompletableFuture<Response> handle(MessageWrapper<T> message);

    MessageType getMessageType();
}
