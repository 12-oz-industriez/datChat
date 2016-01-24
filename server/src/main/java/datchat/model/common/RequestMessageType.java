package datchat.model.common;

public enum RequestMessageType implements MessageType {
    AUTH,
    NEW_MESSAGE,
    GET_LATEST,
    REGISTER
}
