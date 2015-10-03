package datchat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoDatabase;
import datchat.MainVerticle;
import datchat.dao.MessageDao;
import datchat.dao.UserDao;
import datchat.json.ObjectIdSerializer;
import datchat.session.SessionManager;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "datchat")
public class Context {

    @Bean
    public Vertx vertx() {
        return Vertx.vertx();
    }

    @Bean
    public MainVerticle mainVerticle(MessageDao messageDao, UserDao userDao, SessionManager sessionManager) {
        MainVerticle mainVerticle = new MainVerticle(messageDao, userDao, sessionManager);

        vertx().deployVerticle(mainVerticle);

        return mainVerticle;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = Json.mapper;

        SimpleModule module = new SimpleModule();
        module.addSerializer(new ObjectIdSerializer());
        objectMapper.registerModule(module);

        return objectMapper;
    }

    @Bean
    public RedisClient redisClient() {
        JsonObject redisConfig = new JsonObject()
                .put("host", "localhost");

        return RedisClient.create(vertx(), redisConfig);
    }

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create();
    }

    @Bean
    public MongoDatabase mongoDatabase() {
        String databaseName = "datchat";
        return mongoClient().getDatabase(databaseName);
    }
}
