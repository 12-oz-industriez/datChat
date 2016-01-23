package datchat.handlers;

import datchat.dao.MessageDao;
import datchat.filters.common.MessageContext;
import datchat.handlers.common.MessageHandler;
import datchat.handlers.common.Response;
import datchat.model.common.MessageType;
import datchat.model.common.MessageWrapper;
import datchat.model.message.ChatMessage;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

import static datchat.model.common.MessageType.NEW_MESSAGE;

@Component
public class NewMessageHandler implements MessageHandler<ChatMessage> {

    private final MessageDao messageDao;

    @Inject
    public NewMessageHandler(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    @Override
    public CompletableFuture<Response> handle(MessageWrapper<ChatMessage> message, MessageContext messageContext) {
        ChatMessage payload = message.getPayload();
        String id = message.getId();

        CompletableFuture<ChatMessage> future = messageDao.save(payload);

        return future
                .thenApply(savedChatMessage -> {
                    MessageWrapper<ChatMessage> clientResponse = new MessageWrapper<>(id, NEW_MESSAGE, savedChatMessage);
                    MessageWrapper<ChatMessage> broadcastResponse = new MessageWrapper<>(NEW_MESSAGE, savedChatMessage);

                    return new Response(clientResponse, broadcastResponse);
                });
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.NEW_MESSAGE;
    }
}
