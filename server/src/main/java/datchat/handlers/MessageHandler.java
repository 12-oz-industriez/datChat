package datchat.handlers;

import datchat.model.chat.common.BaseMessage;
import datchat.model.chat.common.MessageWrapper;

import java.util.concurrent.CompletableFuture;

public interface MessageHandler<T extends BaseMessage, TR extends BaseMessage, TB extends BaseMessage> {

    CompletableFuture<Response<TR, TB>> handle(MessageWrapper<T> message);

}
