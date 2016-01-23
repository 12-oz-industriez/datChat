package datchat.handlers;

import datchat.dao.MessageDao;
import datchat.filters.common.MessageContext;
import datchat.handlers.common.MessageHandler;
import datchat.handlers.common.Response;
import datchat.model.common.MessageType;
import datchat.model.common.MessageWrapper;
import datchat.model.message.ChatMessage;
import datchat.model.message.GetLatestRequest;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
public class GetLatestHandler implements MessageHandler<GetLatestRequest> {

    private final MessageDao messageDao;

    @Inject
    public GetLatestHandler(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    @Override
    public CompletableFuture<Response> handle(MessageWrapper<GetLatestRequest> message, MessageContext messageContext) {
        String id = message.getId();
        GetLatestRequest payload = message.getPayload();

        return messageDao.getLatestMessages(
                Optional.ofNullable(payload.getLastMessageId()),
                Optional.ofNullable(payload.getCount())
        ).thenApply(messages -> {
            MessageWrapper<List<ChatMessage>> newMessagesWrapper = new MessageWrapper<>(id, MessageType.NEW_MESSAGES, messages);

            return new Response(newMessagesWrapper);
        });
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.GET_LATEST;
    }
}
