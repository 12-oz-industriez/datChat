package datchat.handlers;

import datchat.dao.UserDao;
import datchat.exception.NotUniqueUsernameException;
import datchat.filters.common.MessageContext;
import datchat.handlers.common.CombinedResponse;
import datchat.handlers.common.MessageHandler;
import datchat.model.User;
import datchat.model.common.Request;
import datchat.model.common.RequestMessageType;
import datchat.model.common.Response;
import datchat.model.common.ResponseMessageType;
import datchat.model.message.RegisterRequest;
import datchat.model.message.Status;
import datchat.model.message.StatusResponse;
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
    public CompletableFuture<CombinedResponse> handle(Request<RegisterRequest> message, MessageContext messageContext) {
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
                    StatusResponse response = new StatusResponse(Status.OK);
                    Response<StatusResponse> wrapper = new Response<>(ResponseMessageType.STATUS, response);

                    return new CombinedResponse(wrapper);
                });
    }

    @Override
    public RequestMessageType getMessageType() {
        return RequestMessageType.REGISTER;
    }
}
