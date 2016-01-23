package datchat.exception;

import datchat.handlers.common.Response;
import datchat.model.common.MessageType;
import datchat.model.common.MessageWrapper;
import datchat.model.message.ErrorMessage;
import org.springframework.stereotype.Component;

@Component
public class ExceptionHandler {

    public Response handleThrowable(Throwable t) {
        return new Response(new MessageWrapper<>(MessageType.ERROR, new ErrorMessage(t.getMessage())));
    }

}
