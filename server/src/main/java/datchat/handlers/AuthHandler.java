package datchat.handlers;

import datchat.dao.UserDao;
import datchat.exception.AuthenticationFailedException;
import datchat.filters.common.MessageContext;
import datchat.handlers.common.MessageHandler;
import datchat.handlers.common.Response;
import datchat.model.Session;
import datchat.model.common.MessageType;
import datchat.model.common.MessageWrapper;
import datchat.model.message.AuthRequest;
import datchat.model.message.AuthResponse;
import datchat.session.SessionManager;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

@Component
public class AuthHandler implements MessageHandler<AuthRequest> {

    private final UserDao userDao;

    private final SessionManager sessionManager;

    @Inject
    public AuthHandler(UserDao userDao, SessionManager sessionManager) {
        this.userDao = userDao;
        this.sessionManager = sessionManager;
    }

    @Override
    public CompletableFuture<Response> handle(MessageWrapper<AuthRequest> message, MessageContext messageContext) {
        AuthRequest authRequest = message.getPayload();

        String username = authRequest.getUsername();
        String actualPassword = authRequest.getPassword();

        return this.userDao.getByUsername(username)
                .thenApply(user -> {
                    String expectedPassword = user.getPassword();

                    boolean passwordMatches = BCrypt.checkpw(actualPassword, expectedPassword);

                    if (!passwordMatches) {
                        throw new AuthenticationFailedException();
                    }

                    Session session = this.sessionManager.createSession(user.getId());

                    MessageWrapper<AuthResponse> wrapper = new MessageWrapper<>(message.getId(), MessageType.AUTH, new AuthResponse(session.getSessionId()));

                    return new Response(wrapper);
                });
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.AUTH;
    }
}
