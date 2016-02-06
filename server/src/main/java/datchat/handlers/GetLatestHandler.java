package datchat.handlers;

import datchat.dao.MessageDao;
import datchat.filters.common.MessageContext;
import datchat.handlers.common.CombinedResponse;
import datchat.handlers.common.MessageHandler;
import datchat.mappers.ChatMessageResponseMapper;
import datchat.model.common.Request;
import datchat.model.common.RequestMessageType;
import datchat.model.common.Response;
import datchat.model.common.ResponseMessageType;
import datchat.model.message.ChatMessageResponse;
import datchat.model.message.GetLatestRequest;
import datchat.model.message.NewMessagesResponse;
import org.springframework.stereotype.Component;
import rx.Observable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class GetLatestHandler implements MessageHandler<GetLatestRequest> {

    private final MessageDao messageDao;

    private final ChatMessageResponseMapper chatMessageResponseMapper;

    @Inject
    public GetLatestHandler(MessageDao messageDao, ChatMessageResponseMapper chatMessageResponseMapper) {
        this.messageDao = messageDao;
        this.chatMessageResponseMapper = chatMessageResponseMapper;
    }

    @Override
    public Observable<CombinedResponse> handle(Request<GetLatestRequest> message, MessageContext messageContext) {
        String id = message.getId();
        GetLatestRequest payload = message.getPayload();

        return messageDao.getLatest(
                Optional.ofNullable(payload.getLastMessageId()),
                Optional.ofNullable(payload.getCount()))

                .flatMap(this.chatMessageResponseMapper::map)
                .collect(ArrayList<ChatMessageResponse>::new, List::add)
                .map(chatMessages -> {
                    NewMessagesResponse response = new NewMessagesResponse(chatMessages);
                    Response<NewMessagesResponse> clientResponse = new Response<>(id, ResponseMessageType.NEW_MESSAGES, response);

                    return new CombinedResponse(clientResponse);
                });
    }

    @Override
    public RequestMessageType getMessageType() {
        return RequestMessageType.GET_LATEST;
    }
}
