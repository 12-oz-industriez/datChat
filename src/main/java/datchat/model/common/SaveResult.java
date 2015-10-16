package datchat.model.common;

public class SaveResult extends WebSocketMessage {
    private final SaveStatus status;

    public SaveResult(String id, SaveStatus status) {
        super(id);
        this.status = status;
    }

    public SaveStatus getStatus() {
        return status;
    }
}
