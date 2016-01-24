package datchat.model.message;

import datchat.model.ChatMessage;

import java.util.Collections;
import java.util.List;

public class NewMessagesResponse {
    private final List<ChatMessage> messages;

    public NewMessagesResponse(ChatMessage message) {
        this(Collections.singletonList(message));
    }

    public NewMessagesResponse(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }
}
