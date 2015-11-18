package datchat.exception;

public class AuthenticationFailedException extends RuntimeException {

    public AuthenticationFailedException() { }

    public AuthenticationFailedException(Throwable cause) {
        super(cause);
    }
}
