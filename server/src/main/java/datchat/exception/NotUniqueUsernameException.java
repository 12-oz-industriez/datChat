package datchat.exception;

public class NotUniqueUsernameException extends RuntimeException {
    public NotUniqueUsernameException() {
    }

    public NotUniqueUsernameException(String message) {
        super(message);
    }
}
