package booking_system.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final int errorCode;

    public BaseException(String message) {
        super(message);
        this.errorCode = 500;
    }

    public BaseException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BaseException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

}
