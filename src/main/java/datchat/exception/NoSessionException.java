package datchat.exception;

public class NoSessionException extends RuntimeException {
    public NoSessionException() { }

    public NoSessionException(Throwable cause) {
        super(cause);
    }
}
