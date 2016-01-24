package datchat.model.message;

public class StatusResponse {
    private final Status status;
    private final String errorMessage;

    public StatusResponse(Status status) {
        this(status, null);
    }

    public StatusResponse(Status status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public Status getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
