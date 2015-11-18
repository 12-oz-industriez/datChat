package datchat.handlers;

import datchat.model.chat.common.MessageWrapper;

import java.util.concurrent.CompletableFuture;

public interface MessageHandler<T, TR, TB> {

    CompletableFuture<Response<TR, TB>> handle(MessageWrapper<T> message);

}
