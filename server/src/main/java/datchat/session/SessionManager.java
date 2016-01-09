package datchat.session;

import datchat.model.Session;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public Session createSession(ObjectId userId) {
        Session session = new Session(generateSessionId(), userId);

        this.sessions.put(session.getSessionId(), session);

        return session;
    }

    public Session getBySessionId(String sessionId) {
        return this.sessions.get(sessionId);
    }

    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }
}
