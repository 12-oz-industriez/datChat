package datchat.config;

import datchat.MainVerticle;
import datchat.dao.MessageDao;
import datchat.handlers.MessageDispatcher;
import datchat.handlers.MessageHandler;
import datchat.handlers.NewMessageHandler;
import datchat.model.chat.common.MessageType;
import datchat.model.chat.message.ChatMessage;
import datchat.model.chat.save.SaveResult;
import datchat.routes.WebSocketHandler;
import datchat.routes.WebSocketService;
import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class VertxConfig {

    private static final String WEBSOCKET_PATH = "/chat";

    @Inject
    private MessageDao messageDao;

    @Inject
    @Qualifier("port")
    private Integer port;

    @Bean
    public Vertx vertx() {
        return Vertx.vertx();
    }

    @Bean
    public MainVerticle mainVerticle() {
        MainVerticle mainVerticle = new MainVerticle(webSocketHandler(), port);

        vertx().deployVerticle(mainVerticle);

        return mainVerticle;
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new WebSocketHandler(WEBSOCKET_PATH, webSocketService());
    }

    @Bean
    public WebSocketService webSocketService() {
        return new WebSocketService(dispatcher());
    }

    @Bean
    public MessageDispatcher dispatcher() {
        return new MessageDispatcher(messageHandlers());
    }

    @Bean
    public Map<MessageType, MessageHandler> messageHandlers() {
        Map<MessageType, MessageHandler> messageHandlers = new HashMap<>();

        messageHandlers.put(MessageType.NEW_MESSAGE, newMessageHandler());

        return messageHandlers;
    }

    @Bean
    public MessageHandler<ChatMessage, SaveResult, ChatMessage> newMessageHandler() {
        return new NewMessageHandler(messageDao);
    }
}
