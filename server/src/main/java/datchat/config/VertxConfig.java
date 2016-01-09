package datchat.config;

import datchat.MainVerticle;
import datchat.routes.WebSocketHandler;
import datchat.routes.WebSocketService;
import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Configuration
public class VertxConfig {

    private static final String WEBSOCKET_PATH = "/chat";

    @Inject
    @Qualifier("port")
    private Integer port;

    @Bean
    public Vertx vertx() {
        return Vertx.vertx();
    }

    @Bean
    public MainVerticle mainVerticle(WebSocketService webSocketService) {
        MainVerticle mainVerticle = new MainVerticle(webSocketHandler(webSocketService), port);

        vertx().deployVerticle(mainVerticle);

        return mainVerticle;
    }

    @Bean
    public WebSocketHandler webSocketHandler(WebSocketService webSocketService) {
        return new WebSocketHandler(WEBSOCKET_PATH, webSocketService);
    }
}
