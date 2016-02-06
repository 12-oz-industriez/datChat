package datchat.handlers;

import datchat.dao.MessageDao;
import datchat.filters.common.MessageContext;
import datchat.handlers.common.CombinedResponse;
import datchat.handlers.common.MessageHandler;
import datchat.mappers.ChatMessageResponseMapper;
import datchat.model.ChatMessage;
import datchat.model.common.Request;
import datchat.model.common.RequestMessageType;
import datchat.model.common.Response;
import datchat.model.common.ResponseMessageType;
import datchat.model.message.NewMessagesResponse;
import org.springframework.stereotype.Component;
import rx.Observable;

import javax.inject.Inject;

@Component
public class NewMessageHandler implements MessageHandler<ChatMessage> {

    private final MessageDao messageDao;

    private final ChatMessageResponseMapper chatMessageResponseMapper;

    @Inject
    public NewMessageHandler(MessageDao messageDao, ChatMessageResponseMapper chatMessageResponseMapper) {
        this.messageDao = messageDao;
        this.chatMessageResponseMapper = chatMessageResponseMapper;
    }

    @Override
    public Observable<CombinedResponse> handle(Request<ChatMessage> message, MessageContext messageContext) {
        ChatMessage payload = message.getPayload();
        String id = message.getId();

        return this.messageDao.save(payload)
                .flatMap(this.chatMessageResponseMapper::map)
                .map(savedChatMessage -> {
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
