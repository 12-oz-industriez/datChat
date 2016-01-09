package datchat.dao;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoCollection;
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

    private final MongoCollection<Document> collection;

    @Inject
    public UserDao(MongoDatabase mongoDatabase) {
        this.collection = mongoDatabase.getCollection(COLLECTION_NAME);
    }

    public CompletableFuture<User> getByUsername(String username) {
        CompletableFuture<User> future = new CompletableFuture<>();

        collection.find(eq("username", username))
                .limit(1)
                .map(this::convertToUser)
                .first(defaultCallback(future));

        return future;
    }

    public CompletableFuture<User> getById(ObjectId id) {
        CompletableFuture<User> future = new CompletableFuture<>();

        collection.find(eq("_id", id))
                .limit(1)
                .map(this::convertToUser)
                .first(defaultCallback(future));

        return future;
    }

    public CompletableFuture<User> save(User user) {
        User userWithId = user.toBuilder()
                .withId(new ObjectId())
                .build();

        CompletableFuture<User> future = new CompletableFuture<>();

        collection.insertOne(convertToDocument(user), (result, t) -> {
            if (t != null) {
                future.completeExceptionally(t);
            } else {
                future.complete(userWithId);
            }
        });

        return future;
    }

    private <T> SingleResultCallback<T> defaultCallback(CompletableFuture<T> future) {
        return (r, t) -> {
            if (t != null) {
                future.completeExceptionally(t);
            } else {
                future.complete(r);
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
