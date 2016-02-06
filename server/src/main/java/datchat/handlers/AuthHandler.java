package datchat.handlers;

import datchat.dao.UserDao;
import datchat.exception.AuthenticationFailedException;
import datchat.filters.common.MessageContext;
import datchat.handlers.common.CombinedResponse;
import datchat.handlers.common.MessageHandler;
import datchat.model.Session;
import datchat.model.common.Request;
import datchat.model.common.RequestMessageType;
import datchat.model.common.Response;
import datchat.model.common.ResponseMessageType;
import datchat.model.message.auth.AuthRequest;
import datchat.model.message.auth.AuthResponse;
import datchat.session.SessionManager;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import rx.Observable;

import javax.inject.Inject;

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
    public Observable<CombinedResponse> handle(Request<AuthRequest> message, MessageContext messageContext) {
        AuthRequest authRequest = message.getPayload();

        String username = authRequest.getUsername();
        String actualPassword = authRequest.getPassword();

        return this.userDao.getByUsername(username)
                .map(user -> {
                    String expectedPassword = user.getPassword();

                    boolean passwordMatches = BCrypt.checkpw(actualPassword, expectedPassword);

                    if (!passwordMatches) {
                        throw new AuthenticationFailedException();
                    }

                    Session session = this.sessionManager.createSession(user.getId());

                    Response<AuthResponse> wrapper = new Response<>(message.getId(), ResponseMessageType.AUTH, new AuthResponse(session.getSessionId()));

                    return new CombinedResponse(wrapper);
                });
    }

    @Override
    public RequestMessageType getMessageType() {
        return RequestMessageType.AUTH;
    }
}
