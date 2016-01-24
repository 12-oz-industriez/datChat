package datchat.filters;

import datchat.exception.NoSessionException;
import datchat.filters.common.MessageContext;
import datchat.filters.common.MessageFilter;
import datchat.model.Session;
import datchat.model.common.Request;
import datchat.session.SessionManager;

import javax.inject.Inject;
import java.util.Optional;

public class RestoreSessionFilter implements MessageFilter {
    private final SessionManager sessionManager;

    @Inject
    public RestoreSessionFilter(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public <T> void filter(Request<T> message, MessageContext context) {
        Optional<Session> session = Optional.ofNullable(message.getSessionId())
                .map(this.sessionManager::getBySessionId);

        if (!session.isPresent()) {
            throw new NoSessionException();
        } else {
            context.setSession(session.get());
        }
    }
}
