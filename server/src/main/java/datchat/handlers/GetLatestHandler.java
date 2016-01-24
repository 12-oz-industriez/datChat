package datchat.handlers;

import datchat.dao.MessageDao;
import datchat.filters.common.MessageContext;
import datchat.handlers.common.CombinedResponse;
import datchat.handlers.common.MessageHandler;
import datchat.model.common.Request;
import datchat.model.common.RequestMessageType;
import datchat.model.common.Response;
import datchat.model.common.ResponseMessageType;
import datchat.model.message.GetLatestRequest;
import datchat.model.message.NewMessagesResponse;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
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
    public CompletableFuture<CombinedResponse> handle(Request<GetLatestRequest> message, MessageContext messageContext) {
        String id = message.getId();
        GetLatestRequest payload = message.getPayload();

        return messageDao.getLatestMessages(
                Optional.ofNullable(payload.getLastMessageId()),
                Optional.ofNullable(payload.getCount())
        ).thenApply(messages -> {
            NewMessagesResponse response = new NewMessagesResponse(messages);
            Response<NewMessagesResponse> clientResponse = new Response<>(id, ResponseMessageType.NEW_MESSAGES, response);

            return new CombinedResponse(clientResponse);
        });
    }

    @Override
    public RequestMessageType getMessageType() {
        return RequestMessageType.GET_LATEST;
    }
}
