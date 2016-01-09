package datchat.dao;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import datchat.model.chat.message.ChatMessage;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.mongodb.client.model.Sorts.descending;

@Repository
public class MessageDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDao.class);
    private static final String COLLECTION_NAME = "chatMessages";

    private final MongoCollection<Document> collection;

    @Inject
    public MessageDao(MongoDatabase database) {
        this.collection = database.getCollection(COLLECTION_NAME);
    }

    public void save(ChatMessage chatMessage, Consumer<ChatMessage> resultCallback, Consumer<Throwable> exceptionCallback) {
        ChatMessage chatMessageWithId = chatMessage.toBuilder()
                .withId(new ObjectId())
                .build();

        collection.insertOne(convertToDocument(chatMessage),
                        (result, t) -> {
                            if (t != null) {
                                exceptionCallback.accept(t);
                            } else {
                                resultCallback.accept(chatMessageWithId);
                            }
                        });
    }

    public CompletableFuture<ChatMessage> save(ChatMessage chatMessage) {
        CompletableFuture<ChatMessage> future = new CompletableFuture<>();

        save(chatMessage, future::complete, future::completeExceptionally);

        return future;
    }

    public CompletableFuture<List<ChatMessage>> getLatestMessages(Optional<ObjectId> latestId, Optional<Integer> count) {
        FindIterable<Document> findIterable = collection.find();

        if (latestId.isPresent()) {
            findIterable.filter(Filters.lt("_id", latestId.get()));
        }

        findIterable.sort(descending("_id"));

        if (count.isPresent()) {
            findIterable.limit(count.get());
        }

        CompletableFuture<List<ChatMessage>> future = new CompletableFuture<>();

        findIterable.map(this::convertToMessage)
                .into(new ArrayList<>(), createFutureCallback(future));

        return future;
    }

    private <T> SingleResultCallback<T> createFutureCallback(CompletableFuture<T> future) {
        return (result, t) -> {
            if (t != null) {
                future.completeExceptionally(t);
            } else {
                future.complete(result);
            }
        };
    }

    private Document convertToDocument(ChatMessage chatMessage) {
        return new Document()
                .append("body", chatMessage.getBody())
                .append("author", chatMessage.getAuthor());
    }

    private ChatMessage convertToMessage(Document document) {
        return ChatMessage.builder()
                .withId(document.getObjectId("_id"))
                .withBody(document.getString("body"))
                .withAuthor(document.getString("author"))
                .build();
    }
}
