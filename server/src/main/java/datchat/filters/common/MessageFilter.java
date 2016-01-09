package datchat.filters.common;

import datchat.model.common.MessageWrapper;

public interface MessageFilter {

    <T> MessageWrapper<T> filter(MessageWrapper<T> message, MessageContext context);
}
