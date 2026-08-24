package booking_system.exception;

public class SeatsIsNullException extends BaseException {
    private static final String DEFAULT_MESSAGE = "Не выбрано ни одно место";
    private static final int DEFAULT_ERROR_CODE = 400;

    public SeatsIsNullException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }
}
