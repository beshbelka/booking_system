package booking_system.exception;

public class SeanceNotFoundException extends BaseException {

    private static final String DEFAULT_MESSAGE = "Сеанс не найден";
    private static final int DEFAULT_ERROR_CODE = 404;

    public SeanceNotFoundException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }

    public SeanceNotFoundException(String message) {
        super(message);
    }
}
