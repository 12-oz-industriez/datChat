package datchat.model.common;

public class WebSocketMessage {
    protected final String id;

    public WebSocketMessage(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
