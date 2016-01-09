package datchat.model;

import org.bson.types.ObjectId;

public class Session {
    private final String sessionId;
    private final ObjectId userId;

    public Session(String sessionId, ObjectId userId) {
        this.sessionId = sessionId;
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public ObjectId getUserId() {
        return userId;
    }
}
