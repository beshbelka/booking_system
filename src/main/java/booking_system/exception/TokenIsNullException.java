package booking_system.exception;

public class TokenIsNullException extends BaseException {

    private static final int DEFAULT_ERROR_CODE = 401;
    private static final String DEFAULT_MESSAGE = "Токен отсутствует или пустой";

    public TokenIsNullException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }

}
