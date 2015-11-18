package datchat.model.chat.save;

import datchat.model.chat.annotation.PayloadSubType;
import datchat.model.chat.common.BaseMessage;
import datchat.model.chat.common.MessageType;

@PayloadSubType(MessageType.SAVE_RESULT)
public class SaveResult implements BaseMessage {
    private final SaveStatus status;

    public SaveResult(SaveStatus status) {
        this.status = status;
    }

    public SaveStatus getStatus() {
        return status;
    }
}
