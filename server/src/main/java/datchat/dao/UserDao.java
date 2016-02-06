package datchat.dao;

import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.MongoDatabase;
import datchat.model.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import rx.Observable;

import javax.inject.Inject;

import static com.mongodb.client.model.Filters.eq;

@Repository
public class UserDao {
    private static final String COLLECTION_NAME = "users";

    private final MongoCollection<Document> collection;

    @Inject
    public UserDao(MongoDatabase mongoDatabase) {
        this.collection = mongoDatabase.getCollection(COLLECTION_NAME);
    }

    public Observable<User> getByUsername(String username) {
        return collection.find(eq("username", username))
                .first()
                .map(this::convertToUser);
    }

    public Observable<User> getById(ObjectId id) {
        return collection.find(eq("_id", id))
                .first()
                .map(this::convertToUser);
    }

    public Observable<User> save(User user) {
        User userWithId = user.toBuilder()
                .withId(new ObjectId())
                .build();

        return collection.insertOne(convertToDocument(userWithId))
                .map(s -> userWithId);
    }

    private User convertToUser(Document document) {
        if (document == null) {
            return null;
        }

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
