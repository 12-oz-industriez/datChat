package datchat.filters.common;

import datchat.model.common.MessageWrapper;

public interface MessageFilter {

    <T> void filter(MessageWrapper<T> message, MessageContext context);
}
