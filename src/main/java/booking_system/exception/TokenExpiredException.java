package booking_system.exception;

public class TokenExpiredException extends BaseException {

    private static final String DEFAULT_MESSAGE = "Срок действия токена вышел";
    private static final int DEFAULT_ERROR_CODE = 401;

    public TokenExpiredException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }

}
