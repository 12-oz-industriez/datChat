package datchat.session;

import datchat.exception.NotFoundException;
import datchat.model.Session;
import datchat.model.User;
import io.vertx.redis.RedisClient;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class SessionManager {

    private final RedisClient redisClient;

    @Inject
    public SessionManager(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public CompletableFuture<Session> getSession(String sessionId) {
        CompletableFuture<Session> future = new CompletableFuture<>();
        redisClient.get(sessionId, r -> {
            if (r.failed()) {
                future.completeExceptionally(r.cause());
            } else if (r.result() == null) {
                future.completeExceptionally(new NotFoundException(Session.class, sessionId));
            } else {
                future.complete(new Session(sessionId, r.result()));
            }
        });
        return future;
    }

    public CompletableFuture<Session> createSession(User user) {
        CompletableFuture<Session> future = new CompletableFuture<>();

        String sessionId = generateSessionId();
        String value = user.getId().toHexString();
        redisClient.set(sessionId, value, r -> {
            if (r.succeeded()) {
                future.complete(new Session(sessionId, value));
            } else if (r.failed()) {
                future.completeExceptionally(r.cause());
            }
        });

        return future;
    }

    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }
}
