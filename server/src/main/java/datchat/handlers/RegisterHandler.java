package datchat.handlers;

import datchat.dao.UserDao;
import datchat.exception.NotUniqueUsernameException;
import datchat.filters.common.MessageContext;
import datchat.handlers.common.MessageHandler;
import datchat.handlers.common.Response;
import datchat.model.User;
import datchat.model.common.MessageType;
import datchat.model.common.MessageWrapper;
import datchat.model.message.RegisterRequest;
import datchat.model.message.RegisterResponse;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

@Component
public class RegisterHandler implements MessageHandler<RegisterRequest> {
    private final UserDao userDao;

    @Inject
    public RegisterHandler(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public CompletableFuture<Response> handle(MessageWrapper<RegisterRequest> message, MessageContext messageContext) {
        RegisterRequest payload = message.getPayload();

        String username = payload.getUsername();
        String password = payload.getPassword();

        return this.userDao.getByUsername(username)
                .thenAccept(user -> {
                    if (user != null) {
                        throw new NotUniqueUsernameException("Username " + username + " is not unique");
                    }
                })
                .thenCompose((v) -> this.userDao.save(new User(username, BCrypt.hashpw(password, BCrypt.gensalt()))))
                .thenApply(user -> {
                    RegisterResponse response = new RegisterResponse(user);
                    MessageWrapper<RegisterResponse> wrapper = new MessageWrapper<>(MessageType.REGISTER, response);

                    return new Response(wrapper);
                });
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.REGISTER;
    }
}
