package booking_system.exception;

public class UserNotFoundException extends BaseException {

    private static final String DEFAULT_MESSAGE = "Пользователь не найден";
    private static final int DEFAULT_ERROR_CODE = 401;

    public UserNotFoundException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
