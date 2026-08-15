package booking_system.exception;

public class BookNotFoundException extends BaseException {

    private static final String DEFAULT_MESSAGE = "Бронирование не найдено";
    private static final int DEFAULT_ERROR_CODE = 404;

    public BookNotFoundException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }
    public BookNotFoundException(String message) {
        super(message);
    }
}
