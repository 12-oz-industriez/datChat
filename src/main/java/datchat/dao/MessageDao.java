package datchat.dao;

import com.mongodb.async.client.MongoDatabase;
import datchat.model.Message;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.mongodb.client.model.Sorts.descending;

@Repository
public class MessageDao {
    private static final String COLLECTION_NAME = "collectionName";

    private final MongoDatabase database;

    @Inject
    public MessageDao(MongoDatabase database) {
        this.database = database;
    }

    public CompletableFuture<Void> save(Message message) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        database.getCollection(COLLECTION_NAME)
                .insertOne(convertToDocument(message),
                        (result, t) -> {
                            if (t != null) {
                                future.completeExceptionally(t);
                            } else {
                                future.complete(result);
                            }
                        });

        return future;
    }

    public CompletableFuture<List<Message>> getMessages() {
        CompletableFuture<List<Message>> future = new CompletableFuture<>();

        final List<Message> messages = new ArrayList<>();
        database.getCollection(COLLECTION_NAME)
                .find()
                .sort(descending("_id"))
                .forEach(d -> messages.add(convertToMessage(d)),
                        (r, t) -> {
                            if (t != null) {
                                future.completeExceptionally(t);
                            } else {
                                future.complete(messages);
                            }
                        }
                );

        return future;
    }

    private Document convertToDocument(Message message) {
        return new Document()
                .append("body", message.getBody())
                .append("author", message.getAuthor());
    }

    private Message convertToMessage(Document document) {
        return new Message(
                document.getObjectId("_id"),
                document.getString("body"),
                document.getObjectId("author")
        );
    }
}
