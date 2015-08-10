package datchat;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class Bootstrap extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Router router = Router.router(vertx);

        createRoutes(router);

        String port = System.getenv("PORT");

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(Integer.valueOf(port), result -> {
                    if (result.succeeded()) {
                        startFuture.complete();
                    } else {
                        startFuture.fail(result.cause());
                    }
                });
    }

    private void createRoutes(Router router) {
        router.get("/").handler(StaticHandler.create("assets"));

        router.get("/api/healthcheck").handler(c ->
                c.response()
                        .setStatusCode(200)
                        .end("Hello, world!")
        );
    }
}
