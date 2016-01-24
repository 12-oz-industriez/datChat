package datchat.exception;

import datchat.handlers.common.Response;
import datchat.model.common.MessageType;
import datchat.model.common.MessageWrapper;
import datchat.model.message.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    public Response handleThrowable(Throwable t) {
        LOGGER.error("", t);

        String message = t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName();

        return new Response(new MessageWrapper<>(MessageType.ERROR, new ErrorMessage(message)));
    }

}
