package datchat.handlers.common;

import datchat.exception.ExceptionHandler;
import datchat.filters.common.MessageContext;
import datchat.filters.common.MessageFilter;
import datchat.model.common.Request;
import datchat.model.common.RequestMessageType;
import rx.Observable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageDispatcher {

    private final Map<RequestMessageType, MessageHandler> handlers;

    private final Map<RequestMessageType, List<MessageFilter>> messageFilters;

    private final ExceptionHandler exceptionHandler;

    public MessageDispatcher(List<MessageHandler<?>> handlers,
                             Map<RequestMessageType, List<MessageFilter>> messageFilters,
                             ExceptionHandler exceptionHandler) {
        this.handlers = handlers.stream()
                .collect(HashMap::new,
                        (map, handler) -> map.put(handler.getMessageType(), handler),
                        HashMap::putAll);

        this.messageFilters = messageFilters;
        this.exceptionHandler = exceptionHandler;
    }

    public Observable<CombinedResponse> dispatch(Request message) {
        return doDispatch(message)
                .doOnError(t -> this.exceptionHandler.handleThrowable(message.getId(), t));
    }

    private Observable<CombinedResponse> doDispatch(Request message) {
        RequestMessageType type = message.getType();
        MessageHandler<?> messageHandler = this.handlers.get(type);

        if (messageHandler == null) {
            return Observable.error(new RuntimeException("No handler for " + type + " message type"));
        }

        // filter
        MessageContext messageContext = new MessageContext();
        try {
            messageFilters.getOrDefault(type, Collections.emptyList()).stream()
                    .forEach(filter -> filter.filter(message, messageContext));
        } catch (Exception e) {
            return Observable.error(e);
        }

        // handle
        return messageHandler.handle(message, messageContext);
    }
}
