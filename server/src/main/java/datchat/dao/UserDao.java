package datchat.dao;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoDatabase;
import datchat.model.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

import static com.mongodb.client.model.Filters.eq;

@Repository
public class UserDao {
    private static final String COLLECTION_NAME = "users";

    private final MongoDatabase mongoDatabase;

    @Inject
    public UserDao(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public CompletableFuture<User> getByUsername(String username) {
        CompletableFuture<User> future = new CompletableFuture<>();

        mongoDatabase.getCollection(COLLECTION_NAME)
                .find(eq("username", username))
                .limit(1)
                .forEach(d -> future.complete(convertToUser(d)), defaultCallback(future));

        return future;
    }

    public CompletableFuture<User> getById(ObjectId id) {
        CompletableFuture<User> future = new CompletableFuture<>();

        mongoDatabase.getCollection(COLLECTION_NAME)
                .find(eq("_id", id))
                .limit(1)
                .forEach(d -> future.complete(convertToUser(d)), defaultCallback(future));

        return future;
    }

    public CompletableFuture<Void> save(User user) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        mongoDatabase.getCollection(COLLECTION_NAME)
                .insertOne(convertToDocument(user), defaultCallback(future));

        return future;
    }

    private <T> SingleResultCallback<Void> defaultCallback(CompletableFuture<T> future) {
        return (r, t) -> {
            if (t != null) {
                future.completeExceptionally(t);
            }
        };
    }

    private User convertToUser(Document document) {
        return new User(
                document.getObjectId("_id"),
                document.getString("username"),
                document.getString("password")
        );
    }

    private Document convertToDocument(User user) {
        return new Document()
                .append("username", user.getUsername())
                .append("password", user.getPassword());
    }
}
