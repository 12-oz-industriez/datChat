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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

@Configuration
@ComponentScan(basePackages = "datchat")
public class Context {

    @Value("#{systemEnvironment['MONGOLAB_URI']}")
    private String mongoConnectionString;

    @Value("#{systemEnvironment['REDIS_URL']}")
    private String redisConnectionString;

    @Bean
    public Vertx vertx() {
        return Vertx.vertx();
    }

    @Bean
    public MainVerticle mainVerticle(MessageDao messageDao, UserDao userDao, SessionManager sessionManager) {
        MainVerticle mainVerticle = new MainVerticle(mongoDatabase, messageDao, userDao, sessionManager);

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
        try {
            URI redisURI = new URI(redisConnectionString);

            JsonObject redisConfig = new JsonObject()
                    .put("host", redisURI.getHost())
                    .put("port", redisURI.getPort());

            CompletableFuture<Void> future = new CompletableFuture<>();

            RedisClient redisClient = RedisClient.create(vertx(), redisConfig);
            redisClient.auth(redisURI.getUserInfo().split(":")[1], event -> {
                if (event.succeeded()) {
                    future.complete(null);
                } else if (event.failed()) {
                    future.completeExceptionally(event.cause());
                }
            });

            future.join();

            return redisClient;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoConnectionString);
    }

    @Bean
    public MongoDatabase mongoDatabase() {
        String databaseName = "datchat";
        return mongoClient().getDatabase(databaseName);
    }
}
