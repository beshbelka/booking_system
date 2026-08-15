package booking_system.exception;

public class EmailIsNullException extends BaseException {

    private static final String DEFAULT_MESSAGE = "Email отсутствует или пуст";
    private static final int DEFAULT_ERROR_CODE = 401;

    public EmailIsNullException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }

    public EmailIsNullException(String message) {
        super(message);
    }
}
