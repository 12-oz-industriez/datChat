package datchat.mappers;

import datchat.dao.UserDao;
import datchat.model.ChatMessage;
import datchat.model.User;
import datchat.model.message.ChatMessageResponse;
import org.springframework.stereotype.Component;
import rx.Observable;

import javax.inject.Inject;

@Component
public class ChatMessageResponseMapper {

    private final UserDao userDao;

    @Inject
    public ChatMessageResponseMapper(UserDao userDao) {
        this.userDao = userDao;
    }

    public Observable<ChatMessageResponse> map(ChatMessage chatMessage) {
        return this.userDao.getById(chatMessage.getAuthor())
                .map(user -> this.map(chatMessage, user));
    }

    private ChatMessageResponse map(ChatMessage chatMessage, User user) {
        return ChatMessageResponse.builder()
                .withId(chatMessage.getId())
                .withBody(chatMessage.getBody())
                .withAuthor(user.getUsername())
                .build();
    }
}
