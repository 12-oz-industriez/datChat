package datchat.handlers.common;

import datchat.filters.common.MessageContext;
import datchat.model.common.Request;
import datchat.model.common.RequestMessageType;

import java.util.concurrent.CompletableFuture;

public interface MessageHandler<T> {
    CompletableFuture<CombinedResponse> handle(Request<T> message, MessageContext context);

    RequestMessageType getMessageType();
}
