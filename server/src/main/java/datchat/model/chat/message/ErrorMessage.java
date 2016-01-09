package datchat.model.chat.message;

import datchat.model.chat.annotation.PayloadSubType;
import datchat.model.chat.common.MessageType;

@PayloadSubType(MessageType.ERROR)
public class ErrorMessage {
    private final String errorMessage;

    public ErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
