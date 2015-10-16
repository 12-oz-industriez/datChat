package datchat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import datchat.MainVerticle;
import datchat.dao.MessageDao;
import datchat.routes.WebSocketHandler;
import io.vertx.core.Vertx;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VertxConfig {

    private static final String WEBSOCKET_PATH = "/chat";

    @Bean
    public Vertx vertx() {
        return Vertx.vertx();
    }

    @Bean
    public MainVerticle mainVerticle(MessageDao messageDao, ObjectMapper objectMapper) {
        MainVerticle mainVerticle = new MainVerticle(messageDao, webSocketHandler(messageDao, objectMapper));

        vertx().deployVerticle(mainVerticle);

        return mainVerticle;
    }

    @Bean
    public WebSocketHandler webSocketHandler(MessageDao messageDao, ObjectMapper objectMapper) {
        return new WebSocketHandler(WEBSOCKET_PATH, messageDao);
    }
}
