package datchat.session;

import datchat.dao.UserDao;
import datchat.exception.NoSessionException;
import datchat.model.User;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.RoutingContext;
import org.bson.types.ObjectId;

public class SessionHandler implements Handler<RoutingContext> {
    public static final String SESSION_HEADER = "X-Session";

    private final SessionManager sessionManager;
    private final UserDao userDao;

    public SessionHandler(SessionManager sessionManager, UserDao userDao) {
        this.sessionManager = sessionManager;
        this.userDao = userDao;
    }

    @Override
    public void handle(RoutingContext context) {
        MultiMap headers = context.request().headers();
        String sessionId = headers.get(SESSION_HEADER);

        if (sessionId != null) {
            sessionManager.getSession(sessionId)
                    .thenCompose(s -> {
                        ObjectId id = new ObjectId(s.getUserId());
                        return userDao.getById(id);
                    })
                    .handle((u, t) -> {
                        if (u != null) {
                            context.setUser(new VertxUser(u));
                            context.next();
                        } else if (t != null) {
                            context.fail(new NoSessionException(t));
                        }
                        return null;
                    });
        } else {
            context.fail(new NoSessionException());
        }
    }

    private static class VertxUser implements io.vertx.ext.auth.User {

        private final User user;
        private AuthProvider authProvider;

        private VertxUser(User user) {
            this.user = user;
        }

        @Override
        public io.vertx.ext.auth.User isAuthorised(String authority, Handler<AsyncResult<Boolean>> resultHandler) {
            resultHandler.handle(Future.succeededFuture(true));
            return this;
        }

        @Override
        public io.vertx.ext.auth.User clearCache() {
            return this;
        }

        @Override
        public JsonObject principal() {
            String json = io.vertx.core.json.Json.encode(user);
            return new JsonObject(json);
        }

        @Override
        public void setAuthProvider(AuthProvider authProvider) {
            this.authProvider = authProvider;
        }
    }
}
