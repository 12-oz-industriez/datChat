package datchat.filters;

import datchat.exception.NoSessionException;
import datchat.filters.common.MessageContext;
import datchat.filters.common.MessageFilter;
import datchat.model.Session;
import datchat.model.common.MessageWrapper;
import datchat.session.SessionManager;

import javax.inject.Inject;

public class AuthFilter implements MessageFilter {
    private final SessionManager sessionManager;

    @Inject
    public AuthFilter(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public <T> MessageWrapper<T> filter(MessageWrapper<T> message, MessageContext context) {
        String sessionId = message.getSessionId();

        // TODO: rewrite chain of null checks to Optional
        if (sessionId == null) {
            throw new NoSessionException();
        }

        Session session = this.sessionManager.getBySessionId(sessionId);
        if (session == null) {
            throw new NoSessionException();
        }

        context.setSession(session);

        return message;
    }
}
