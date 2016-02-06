package datchat.dao;

import com.mongodb.client.model.Filters;
import com.mongodb.rx.client.FindObservable;
import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.MongoDatabase;
import datchat.model.ChatMessage;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import rx.Observable;

import javax.inject.Inject;
import java.util.Optional;

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

    public Observable<ChatMessage> getById(ObjectId id) {
        return this.collection.find(Filters.eq("_id", id))
                .first()
                .map(this::convertToMessage);
    }

    public Observable<ChatMessage> getLatest(Optional<ObjectId> latestId, Optional<Integer> count) {
        FindObservable<Document> find = collection.find();

        latestId.ifPresent(v -> find.filter(Filters.lt("_id", v)));
        count.ifPresent(find::limit);

        return find.sort(descending("_id"))
                .toObservable()
                .map(this::convertToMessage);
    }

    public Observable<ChatMessage> save(ChatMessage chatMessage) {
        ChatMessage chatMessageWithId = chatMessage.toBuilder()
                .withId(new ObjectId())
                .build();

        return collection.insertOne(convertToDocument(chatMessageWithId))
                .map(s -> chatMessageWithId);
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
                .withAuthor(document.getObjectId("author"))
                .build();
    }
}
