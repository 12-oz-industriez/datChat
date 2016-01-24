package datchat.handlers;

import datchat.dao.MessageDao;
import datchat.filters.common.MessageContext;
import datchat.handlers.common.CombinedResponse;
import datchat.handlers.common.MessageHandler;
import datchat.model.ChatMessage;
import datchat.model.common.Request;
import datchat.model.common.RequestMessageType;
import datchat.model.common.Response;
import datchat.model.common.ResponseMessageType;
import datchat.model.message.NewMessagesResponse;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

@Component
public class NewMessageHandler implements MessageHandler<ChatMessage> {

    private final MessageDao messageDao;

    @Inject
    public NewMessageHandler(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    @Override
    public CompletableFuture<CombinedResponse> handle(Request<ChatMessage> message, MessageContext messageContext) {
        ChatMessage payload = message.getPayload();
        String id = message.getId();

        CompletableFuture<ChatMessage> future = messageDao.save(payload);

        return future
                .thenApply(savedChatMessage -> {
                    NewMessagesResponse response = new NewMessagesResponse(savedChatMessage);
                    Response<NewMessagesResponse> clientResponse = new Response<>(id, ResponseMessageType.NEW_MESSAGES, response);
                    Response<NewMessagesResponse> broadcastResponse = new Response<>(ResponseMessageType.NEW_MESSAGES, response);

                    return new CombinedResponse(clientResponse, broadcastResponse);
                });
    }

    @Override
    public RequestMessageType getMessageType() {
        return RequestMessageType.NEW_MESSAGE;
    }
}
