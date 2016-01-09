package datchat.handlers;

import datchat.dao.MessageDao;
import datchat.handlers.common.MessageHandler;
import datchat.handlers.common.Response;
import datchat.model.chat.common.MessageType;
import datchat.model.chat.common.MessageWrapper;
import datchat.model.chat.message.ChatMessage;
import datchat.model.chat.message.ErrorMessage;
import datchat.model.chat.message.GetLatestRequest;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static datchat.model.chat.common.MessageType.ERROR;

@Component
public class GetLatestHandler implements MessageHandler<GetLatestRequest> {

    private final MessageDao messageDao;

    @Inject
    public GetLatestHandler(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    @Override
    public CompletableFuture<Response> handle(MessageWrapper<GetLatestRequest> message) {
        String id = message.getId();
        GetLatestRequest payload = message.getPayload();

        return messageDao.getLatestMessages(
                Optional.ofNullable(payload.getLastMessageId()),
                Optional.ofNullable(payload.getCount())
        ).thenApply(messages -> {
            MessageWrapper<List<ChatMessage>> newMessagesWrapper = new MessageWrapper<>(id, MessageType.NEW_MESSAGES, messages);

            return new Response(newMessagesWrapper);
        }).exceptionally(t -> {
            return new Response(
                    new MessageWrapper<>(id, ERROR, new ErrorMessage(t.getMessage()))
            );
        });
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.GET_LATEST;
    }
}
