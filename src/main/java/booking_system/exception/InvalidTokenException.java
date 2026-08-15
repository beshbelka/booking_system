package booking_system.exception;

public class InvalidTokenException extends BaseException {

    private static final String DEFAULT_MESSAGE = "Невалидный токен";
    private static final int DEFAULT_ERROR_CODE = 401;

    public InvalidTokenException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }
}
