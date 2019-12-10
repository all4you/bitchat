package io.bitchat.server.http.exception;

/**
 * ValidationException
 *
 * @author houyi
 */
public class ValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ValidationException(String s) {
        super(s);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}
