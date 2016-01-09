package datchat.model.message;

import datchat.model.annotation.PayloadSubType;
import datchat.model.common.MessageType;

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
