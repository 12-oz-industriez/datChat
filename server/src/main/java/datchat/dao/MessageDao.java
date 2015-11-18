package datchat.dao;

import com.mongodb.async.client.MongoDatabase;
import datchat.model.chat.message.ChatMessage;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.mongodb.client.model.Sorts.descending;

@Repository
public class MessageDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDao.class);
    private static final String COLLECTION_NAME = "chatMessages";

    private final MongoDatabase database;

    @Inject
    public MessageDao(MongoDatabase database) {
        this.database = database;
    }

    public void save(ChatMessage chatMessage, Consumer<ChatMessage> resultCallback, Consumer<Throwable> exceptionCallback) {
        ChatMessage chatMessageWithId = chatMessage.toBuilder()
                .withId(new ObjectId())
                .build();

        database.getCollection(COLLECTION_NAME)
                .insertOne(convertToDocument(chatMessage),
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

    public void getMessages(Consumer<List<ChatMessage>> resultCallback,
                            Consumer<Throwable> exceptionCallback) {
        final List<ChatMessage> chatMessages = new ArrayList<>();
        database.getCollection(COLLECTION_NAME)
                .find()
                .sort(descending("_id"))
                .forEach(d -> chatMessages.add(convertToMessage(d)),
                        (r, t) -> {
                            if (t != null) {
                                exceptionCallback.accept(t);
                            } else {
                                resultCallback.accept(chatMessages);
                            }
                        }
                );
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
