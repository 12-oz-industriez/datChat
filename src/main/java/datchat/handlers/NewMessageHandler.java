package datchat.handlers;

import datchat.dao.MessageDao;
import datchat.model.chat.common.MessageWrapper;
import datchat.model.chat.message.ChatMessage;
import datchat.model.chat.save.SaveResult;
import datchat.model.chat.save.SaveStatus;

import java.util.concurrent.CompletableFuture;

import static datchat.model.chat.common.MessageType.NEW_MESSAGE;
import static datchat.model.chat.common.MessageType.SAVE_RESULT;

public class NewMessageHandler implements MessageHandler<ChatMessage, SaveResult, ChatMessage> {

    private final MessageDao messageDao;

    public NewMessageHandler(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    @Override
    public CompletableFuture<Response<SaveResult, ChatMessage>> handle(MessageWrapper<ChatMessage> message) {
        ChatMessage payload = message.getPayload();
        String id = message.getId();

        CompletableFuture<ChatMessage> future = messageDao.save(payload);

        return future
                .thenApply(savedChatMessage -> {
                    MessageWrapper<SaveResult> clientResponseMessage = new MessageWrapper<>(id, SAVE_RESULT, new SaveResult(SaveStatus.OK));
                    MessageWrapper<ChatMessage> broadcastResponseMessage = new MessageWrapper<>(NEW_MESSAGE, savedChatMessage);

                    return new Response<>(clientResponseMessage, broadcastResponseMessage);
                })
                .exceptionally(t -> new Response<>(
                        new MessageWrapper<>(id, SAVE_RESULT, new SaveResult(SaveStatus.ERROR)),
                        null
                ));
    }
}
