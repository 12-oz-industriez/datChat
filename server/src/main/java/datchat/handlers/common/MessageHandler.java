package datchat.handlers.common;

import datchat.filters.common.MessageContext;
import datchat.model.common.Request;
import datchat.model.common.RequestMessageType;
import rx.Observable;

public interface MessageHandler<T> {
    Observable<CombinedResponse> handle(Request<T> message, MessageContext context);

    RequestMessageType getMessageType();
}
