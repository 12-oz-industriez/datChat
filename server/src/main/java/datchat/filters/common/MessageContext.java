package datchat.filters.common;

import datchat.model.Session;

public class MessageContext {
    private Session session;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
