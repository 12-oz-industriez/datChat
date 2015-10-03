package datchat;

import com.mongodb.async.client.MongoDatabase;
import datchat.dao.MessageDao;
import datchat.dao.UserDao;
import datchat.exception.AuthenticationFailedException;
import datchat.model.Message;
import datchat.model.User;
import datchat.session.SessionHandler;
import datchat.session.SessionManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.stream.Collectors;

@Component
public class MainVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

    private final MongoDatabase mongoDatabase;
    private final MessageDao messageDao;
    private final UserDao userDao;
    private final SessionManager sessionManager;

    @Inject
    public MainVerticle(MongoDatabase mongoDatabase, MessageDao messageDao,
                        UserDao userDao, SessionManager sessionManager) {
        this.mongoDatabase = mongoDatabase;
        this.messageDao = messageDao;
        this.userDao = userDao;
        this.sessionManager = sessionManager;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Router router = Router.router(vertx);

        createRoutes(router);

        Integer port;
        String herokuPortEnvVariable = System.getenv("PORT");
        if (herokuPortEnvVariable != null) {
            port = Integer.parseInt(herokuPortEnvVariable);
        } else {
            port = 8080;
        }

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(port, result -> {
                    if (result.succeeded()) {
                        startFuture.complete();
                    } else {
                        startFuture.fail(result.cause());
                    }
                });
    }

    private void createRoutes(Router router) {
        router.exceptionHandler(exc -> LOGGER.error("Exception", exc));

        router.get("/healthcheck").handler(context ->
                mongoDatabase.getCollection("healthcheck")
                        .insertOne(new Document("health", 1), (result, t) -> {
                            if (t != null) {
                                context.response().setStatusCode(500);
                            } else {
                                context.response().setStatusCode(200);
                            }
                        }));

        router.routeWithRegex(HttpMethod.POST, ".*").handler(BodyHandler.create());

        router.route(HttpMethod.POST, "/api/auth").handler(authHandler());
//        router.route().handler(new SessionHandler(sessionManager, userDao));

        router.get("/").handler(StaticHandler.create("assets"));
        router.get("/js/*").handler(StaticHandler.create("assets/js"));
        router.get("/css/*").handler(StaticHandler.create("assets/css"));

        router.post("/api/chat/").handler(createMessageHandler());
        router.get("/api/chat").handler(getMessagesHandler());
    }

    private Handler<RoutingContext> getMessagesHandler() {
        return c ->
                messageDao.getMessages()
                        .thenApply(messages -> messages.stream()
                                .map(Json::encode)
                                .collect(Collectors.toList()))
                        .thenApply(messages -> messages.stream()
                                .collect(Buffer::buffer, Buffer::appendString, Buffer::appendBuffer))
                        .handle((buf, t) -> {
                            HttpServerResponse response = c.response();
                            if (t != null) {
                                response.setStatusCode(500);
                            } else {
                                response.setStatusCode(200)
                                        .write(buf);
                            }
                            response.end();
                            return null;
                        });
    }

    private Handler<RoutingContext> createMessageHandler() {
        return c -> {
            Message message = Json.decodeValue(c.getBodyAsString(), Message.class);

            messageDao.save(message)
                    .handle((r, t) -> {
                        HttpServerResponse response = c.response();
                        if (t != null) {
                            response.setStatusCode(500);
                        } else {
                            response.setStatusCode(201);
                        }
                        return null;
                    });
        };
    }

    private Handler<RoutingContext> authHandler() {
        return c -> {
            String body = c.getBodyAsString();
            User user = Json.decodeValue(body, User.class);

            userDao.getByUsername(user.getUsername())
                    .thenCompose(u -> {
                        if (u == null || !u.getPassword().equals(user.getPassword())) {
                            throw new AuthenticationFailedException();
                        }
                        return sessionManager.createSession(u);
                    })
                    .handle((s, t) -> {
                        HttpServerResponse response = c.response();
                        if (t != null) {
                            response.setStatusCode(401);
                        } else {
                            response.putHeader(SessionHandler.SESSION_HEADER, s.getSessionId());
                            response.setStatusCode(200);
                        }
                        response.end();
                        return null;
                    });
        };
    }
}
