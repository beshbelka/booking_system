package booking_system.exception;

public class MovieNotFoundException extends BaseException {

    private static final String DEFAULT_MESSAGE = "Фильм не найден";
    private static final int DEFAULT_ERROR_CODE = 404;

    public MovieNotFoundException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }
    public MovieNotFoundException(String message) {
        super(message);
    }
}
