package datchat.config.heroku;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

@Configuration
@Profile("heroku")
public class HerokuConfig {

    @Inject
    private Vertx vertx;

    @Value("#{systemEnvironment['MONGOLAB_URI']}")
    private String mongoConnectionString;

    @Value("#{systemEnvironment['REDIS_URL']}")
    private String redisConnectionString;

    @Value("#{systemEnvironment['PORT']}")
    private Integer port;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoConnectionString);
    }

    @Bean
    public RedisClient redisClient() {
        try {
            URI redisURI = new URI(redisConnectionString);

            JsonObject redisConfig = new JsonObject()
                    .put("host", redisURI.getHost())
                    .put("port", redisURI.getPort());

            CompletableFuture<Void> future = new CompletableFuture<>();

            RedisClient redisClient = RedisClient.create(vertx, redisConfig);
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

    @Bean(name = "port")
    public Integer herokuPort() {
        return this.port;
    }
}
