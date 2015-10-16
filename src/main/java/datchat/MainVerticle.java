package datchat;

import datchat.dao.MessageDao;
import datchat.routes.WebSocketHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class MainVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

    private final MessageDao messageDao;
    private final WebSocketHandler webSocketHandler;

    @Inject
    public MainVerticle(MessageDao messageDao,
                        WebSocketHandler webSocketHandler) {
        this.messageDao = messageDao;
        this.webSocketHandler = webSocketHandler;
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
                .websocketHandler(webSocketHandler)
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

        router.routeWithRegex(HttpMethod.POST, ".*").handler(BodyHandler.create());

        router.get("/").handler(StaticHandler.create("assets"));
        router.get("/js/*").handler(StaticHandler.create("assets/js"));
        router.get("/css/*").handler(StaticHandler.create("assets/css"));
    }
}
