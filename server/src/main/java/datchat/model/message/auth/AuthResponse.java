package datchat.model.message.auth;

public class AuthResponse {
    private final String sessionId;

    public AuthResponse(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
