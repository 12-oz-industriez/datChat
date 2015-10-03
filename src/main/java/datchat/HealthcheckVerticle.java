package datchat;

import com.mongodb.async.client.MongoDatabase;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.redis.RedisClient;
import org.bson.Document;

import javax.inject.Inject;

public class HealthcheckVerticle extends AbstractVerticle {
    private final MongoDatabase mongoDatabase;
    private final RedisClient redisClient;

    @Inject
    public HealthcheckVerticle(MongoDatabase mongoDatabase, RedisClient redisClient) {
        this.mongoDatabase = mongoDatabase;
        this.redisClient = redisClient;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Router router = Router.router(vertx);

        router.get("/healthcheck").handler(context ->
                mongoDatabase.getCollection("healthcheck")
                        .insertOne(new Document("health", 1), (result, t) -> {
                            if (t != null) {
                                context.response().setStatusCode(500);
                            } else {
                                context.response().setStatusCode(200);
                            }
                        }));


    }
}
