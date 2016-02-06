package datchat.model.message;

import java.util.Collections;
import java.util.List;

public class NewMessagesResponse {
    private final List<ChatMessageResponse> messages;

    public NewMessagesResponse(ChatMessageResponse message) {
        this(Collections.singletonList(message));
    }

    public NewMessagesResponse(List<ChatMessageResponse> messages) {
        this.messages = messages;
    }

    public List<ChatMessageResponse> getMessages() {
        return messages;
    }
}
