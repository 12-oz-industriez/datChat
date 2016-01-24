package datchat.filters.common;

import datchat.model.common.Request;

public interface MessageFilter {

    <T> void filter(Request<T> message, MessageContext context);
}
