package datchat.model.common;

public class Request<T> extends BaseMessageWrapper<T, RequestMessageType> {

    private RequestMessageType type;

    public Request() {
    }

    @Override
    public RequestMessageType getType() {
        return type;
    }

    public void setType(RequestMessageType type) {
        this.type = type;
    }
}
