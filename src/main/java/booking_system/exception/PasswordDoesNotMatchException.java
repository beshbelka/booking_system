package booking_system.exception;

public class PasswordDoesNotMatchException extends BaseException {

    private static final String DEFAULT_MESSAGE = "Пароль не совпадает";
    private static final int DEFAULT_ERROR_CODE = 401;

    public PasswordDoesNotMatchException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }
}
