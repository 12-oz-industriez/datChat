package datchat;

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

    private final WebSocketHandler webSocketHandler;

    private final Integer port;

    @Inject
    public MainVerticle(WebSocketHandler webSocketHandler,
                        Integer port) {
        this.webSocketHandler = webSocketHandler;
        this.port = port;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Router router = Router.router(vertx);

        createRoutes(router);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .websocketHandler(webSocketHandler)
                .listen(this.port, result -> {
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
