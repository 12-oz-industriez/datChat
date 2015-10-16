package datchat.dao;

import com.mongodb.async.client.MongoDatabase;
import datchat.model.ChatMessage;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.mongodb.client.model.Sorts.descending;

@Repository
public class MessageDao {
    private static final String COLLECTION_NAME = "collectionName";

    private final MongoDatabase database;

    @Inject
    public MessageDao(MongoDatabase database) {
        this.database = database;
    }

    public void save(ChatMessage chatMessage, Consumer<ChatMessage> resultCallback, Consumer<Throwable> exceptionCallback) {
        ChatMessage messageWithId = chatMessage.withChatMessageId(new ObjectId());
        database.getCollection(COLLECTION_NAME)
                .insertOne(convertToDocument(chatMessage),
                        (result, t) -> {
                            if (t != null) {
                                exceptionCallback.accept(t);
                            } else {
                                resultCallback.accept(messageWithId);
                            }
                        });
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
        return new ChatMessage(
                null,
                document.getObjectId("_id"),
                document.getString("body"),
                document.getString("author")
        );
    }
}
