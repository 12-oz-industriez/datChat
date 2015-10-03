package datchat.config.local;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.inject.Inject;

@Configuration
@Profile("local")
public class LocalConfig {

    @Inject
    private Vertx vertx;

    private final String mongoConnectionString = "mongodb://localhost:27017";

    private final String redisHost = "localhost";

    private final Integer redisPort = 6379;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoConnectionString);
    }

    @Bean
    public RedisClient redisClient() {
        JsonObject redisConfig = new JsonObject()
                .put("host", redisHost)
                .put("port", redisPort);

        return RedisClient.create(vertx, redisConfig);
    }
}
