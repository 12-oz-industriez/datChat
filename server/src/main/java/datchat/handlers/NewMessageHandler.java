package datchat.handlers;

import datchat.dao.MessageDao;
import datchat.handlers.common.MessageHandler;
import datchat.handlers.common.Response;
import datchat.model.chat.common.MessageType;
import datchat.model.chat.common.MessageWrapper;
import datchat.model.chat.message.ChatMessage;
import datchat.model.chat.message.ErrorMessage;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

import static datchat.model.chat.common.MessageType.ERROR;
import static datchat.model.chat.common.MessageType.NEW_MESSAGE;

@Component
public class NewMessageHandler implements MessageHandler<ChatMessage> {

    private final MessageDao messageDao;

    @Inject
    public NewMessageHandler(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    @Override
    public CompletableFuture<Response> handle(MessageWrapper<ChatMessage> message) {
        ChatMessage payload = message.getPayload();
        String id = message.getId();

        CompletableFuture<ChatMessage> future = messageDao.save(payload);

        return future
                .thenApply(savedChatMessage -> {
                    MessageWrapper<ChatMessage> clientResponse = new MessageWrapper<>(id, NEW_MESSAGE, savedChatMessage);
                    MessageWrapper<ChatMessage> broadcastResponse = new MessageWrapper<>(NEW_MESSAGE, savedChatMessage);

                    return new Response(clientResponse, broadcastResponse);
                })
                .exceptionally(t -> new Response(
                        new MessageWrapper<>(id, ERROR, new ErrorMessage(t.getMessage()))
                ));
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.NEW_MESSAGE;
    }
}
