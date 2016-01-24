package datchat.exception;

import datchat.handlers.common.CombinedResponse;
import datchat.model.common.Response;
import datchat.model.common.ResponseMessageType;
import datchat.model.message.status.Status;
import datchat.model.message.status.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    public CombinedResponse handleThrowable(String messageId, Throwable t) {
        LOGGER.error("", t);

        String message = t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName();

        StatusResponse response = new StatusResponse(Status.ERROR, message);
        return new CombinedResponse(new Response<>(messageId, ResponseMessageType.STATUS, response));
    }

}
