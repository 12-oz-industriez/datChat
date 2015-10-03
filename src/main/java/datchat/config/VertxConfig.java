package datchat.config;

import com.mongodb.async.client.MongoDatabase;
import datchat.MainVerticle;
import datchat.dao.MessageDao;
import datchat.dao.UserDao;
import datchat.session.SessionManager;
import io.vertx.core.Vertx;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VertxConfig {

    @Bean
    public Vertx vertx() {
        return Vertx.vertx();
    }

    @Bean
    public MainVerticle mainVerticle(MongoDatabase mongoDatabase, MessageDao messageDao, UserDao userDao, SessionManager sessionManager) {
        MainVerticle mainVerticle = new MainVerticle(mongoDatabase, messageDao, userDao, sessionManager);

        vertx().deployVerticle(mainVerticle);

        return mainVerticle;
    }

}
